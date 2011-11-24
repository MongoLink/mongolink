package fr.bodysplash.mongolink;

import com.google.common.collect.Lists;
import com.mongodb.*;
import fr.bodysplash.mongolink.domain.criteria.*;
import fr.bodysplash.mongolink.domain.mapper.ContextBuilder;
import fr.bodysplash.mongolink.test.entity.*;
import fr.bodysplash.mongolink.test.factory.TestFactory;
import org.bson.types.ObjectId;
import org.junit.*;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

@SuppressWarnings("unchecked")
public class TestsIntegration {

    @BeforeClass
    public static void beforeClass() {
        ContextBuilder builder = new ContextBuilder("fr.bodysplash.mongolink.test.integrationMapping");
        sessionManager = MongoSessionManager.create(builder, Settings.defaultInstance());
        mongoSession = sessionManager.createSession();
        db = mongoSession.getDb();
    }

    private static void initData() {
        BasicDBObject fakeEntity = new BasicDBObject();
        fakeEntity.put("_id", new ObjectId("4d9d9b5e36a9a4265ea9ecbe"));
        fakeEntity.put("value", "fake entity value");
        fakeEntity.put("comments", new BasicDBList());
        fakeEntity.put("index", 42);

        BasicDBObject fakeChild = new BasicDBObject();
        fakeChild.put("_id", new ObjectId("5d9d9b5e36a9a4265ea9ecbe"));
        fakeChild.put("childName", "child value");
        fakeChild.put("value", "parent value");
        fakeChild.put("comments", new BasicDBList());
        fakeChild.put("index", 0);
        fakeChild.put("__discriminator", "FakeChildEntity");

        db.getCollection("fakeentity").insert(fakeEntity, fakeChild);

        BasicDBObject naturalIdEntity = new BasicDBObject();
        naturalIdEntity.put("_id", "naturalkey");
        naturalIdEntity.put("value", "naturalvalue");

        db.getCollection("fakeentitywithnaturalid").insert(naturalIdEntity);
    }

    @AfterClass
    public static void afterClass() {
        db.dropDatabase();
        sessionManager.close();
    }

    @Before
    public void before() {
        initData();
        mongoSession.start();
    }

    @After
    public void after() {
        mongoSession.stop();
        db.getCollection("fakeentity").remove(new BasicDBObject());
        db.getCollection("fakeentitywithnaturalid").remove(new BasicDBObject());
    }

    @Test
    public void canGetById() {
        FakeEntity entityFound = mongoSession.get("4d9d9b5e36a9a4265ea9ecbe", FakeEntity.class);

        assertThat(entityFound, notNullValue());
        assertThat(entityFound.getId(), is("4d9d9b5e36a9a4265ea9ecbe"));
        assertThat(entityFound.getValue(), is("fake entity value"));
    }

    @Test
    public void canGetByNaturalId() {
        FakeEntityWithNaturalId fakeEntityWithNaturalId = mongoSession.get("naturalkey", FakeEntityWithNaturalId.class);

        assertThat(fakeEntityWithNaturalId, notNullValue());
        assertThat(fakeEntityWithNaturalId.getNaturalKey(), is("naturalkey"));
    }

    @Test
    public void canUserSessionManager() {
        ContextBuilder contextBuilder = TestFactory.contextBuilder().withFakeEntity();
        MongoSessionManager manager = MongoSessionManager.create(contextBuilder, Settings.defaultInstance());
        MongoSession session = manager.createSession();
        session.save(new FakeEntity("new fake entity"));
    }

    @Test
    public void insertingSetId() {
        DBCollection testid = db.getCollection("testid");
        BasicDBObject dbo = new BasicDBObject();

        testid.insert(Lists.<DBObject>newArrayList(dbo));

        assertThat(dbo.get("_id"), notNullValue());
    }

    @Test
    public void canGetChildEntity() {
        FakeChildEntity entity = (FakeChildEntity) mongoSession.get("5d9d9b5e36a9a4265ea9ecbe", FakeEntity.class);

        assertThat(entity, notNullValue());
        assertThat(entity.getChildName(), is("child value"));
        assertThat(entity.getValue(), is("parent value"));
    }

    @Test
    public void canSaveChildEntity() {
        FakeChildEntity fakeChildEntity = new FakeChildEntity();
        fakeChildEntity.setChildName("child");
        fakeChildEntity.setValue("value from parent");
        fakeChildEntity.addComment("this is a comment!");

        mongoSession.save(fakeChildEntity);

        FakeChildEntity entityFound = mongoSession.get(fakeChildEntity.getId(), FakeChildEntity.class);

        assertThat(entityFound, notNullValue());
        assertThat(entityFound.getComments().size(), is(1));
    }

    @Test
    public void canGetByEqCriteria() {
        final Criteria criteria = mongoSession.createCriteria(FakeEntity.class);
        criteria.add(Restrictions.equals("value", "fake entity value"));

        final List<FakeEntity> list = criteria.list();

        assertThat(list.size(), is(1));
    }

    @Test
    public void canGetByBetweenCriteria() {
        final Criteria criteria = mongoSession.createCriteria(FakeEntity.class);
        criteria.add(Restrictions.between("index", 42, 44));

        final List<FakeEntity> list = criteria.list();

        assertThat(list.size(), is(1));
    }

    @Test
    public void canGetByBetweenCriteriaWithBadBoundaries() {
        final Criteria criteria = mongoSession.createCriteria(FakeEntity.class);
        criteria.add(Restrictions.between("index", 40, 42));

        final List<FakeEntity> list = criteria.list();

        assertThat(list.size(), is(0));
    }

    @Test
    public void cappedCollectionDropItems() throws InterruptedException {
        for (int i = 0; i < 100; i++) {
            BasicDBObject naturalIdEntity = new BasicDBObject();
            naturalIdEntity.put("_id", "cappedid" + i);
            naturalIdEntity.put("value", "cappedvalue" + i);
            db.getCollection("fakeentitywithcap").insert(naturalIdEntity);
        }

        assertThat(db.getCollection("fakeentitywithcap").isCapped(), is(true));
        assertThat(db.getCollection("fakeentitywithcap").count(), is(50L));
    }

    @Test
    public void canLimitSearch() {
        for (int i = 0; i < 10; i++) {
            mongoSession.save(new FakeEntity("valeur"));
        }
        final Criteria criteria = mongoSession.createCriteria(FakeEntity.class);
        criteria.add(Restrictions.equals("value", "valeur"));
        criteria.limit(1);

        final List result = criteria.list();

        assertThat(result.size(), is(1));
    }

    @Test
    public void canSkipSearch() {
        for (int i = 0; i < 10; i++) {
            mongoSession.save(new FakeEntity("valeur"));
        }
        final Criteria criteria = mongoSession.createCriteria(FakeEntity.class);
        criteria.add(Restrictions.equals("value", "valeur"));
        criteria.skip(1);

        final List result = criteria.list();

        assertThat(result.size(), is(9));
    }

    private static DB db;
    private static MongoSession mongoSession;
    private static MongoSessionManager sessionManager;
}
