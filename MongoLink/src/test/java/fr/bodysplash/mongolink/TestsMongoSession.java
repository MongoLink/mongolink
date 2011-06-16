package fr.bodysplash.mongolink;


import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.FakeDB;
import com.mongodb.FakeDBCollection;
import fr.bodysplash.mongolink.criteria.Criteria;
import fr.bodysplash.mongolink.mapper.ContextBuilder;
import fr.bodysplash.mongolink.test.entity.Comment;
import fr.bodysplash.mongolink.test.entity.FakeEntity;
import fr.bodysplash.mongolink.test.entity.FakeEntityWithNaturalId;
import org.bson.types.ObjectId;
import org.hamcrest.Matchers;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class TestsMongoSession {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void before() {
        db = Mockito.spy(new FakeDB());
        entities = new FakeDBCollection(db, "entity");
        fakeEntities = new FakeDBCollection(db, "fakeentitywithnaturalid");
        db.collections.put("fakeentity", entities);
        db.collections.put("fakeentitywithnaturalid", fakeEntities);
        ContextBuilder cb = new ContextBuilder("fr.bodysplash.mongolink.test.simpleMapping");
        session = new MongoSession(db);
        session.setMappingContext(cb.createContext());
    }

    @Test
    public void startAndStopASession() {
        MongoSession session = new MongoSession(db);

        session.start();
        session.stop();

        InOrder inorder = Mockito.inOrder(db);
        inorder.verify(db).requestStart();
        inorder.verify(db).requestDone();
    }

    @Test
    public void canGetByAutoId() {
        createEntity("4d53b7118653a70549fe1b78", "plop");
        createEntity("4d53b7118653a70549fe1b78", "plap");


        FakeEntity entity = session.get("4d53b7118653a70549fe1b78", FakeEntity.class);

        Assert.assertThat(entity, Matchers.notNullValue());
        Assert.assertThat(entity.getValue(), is("plop"));
    }

    @Test
    public void cantGetSomethingWichIsNotAnEntity() {
        exception.expect(MongoLinkError.class);
        exception.expectMessage("Comment is not an entity");
        session.get("pouet", Comment.class);
    }

    @Test
    public void canGetByNaturalId() {
        DBObject dbo = new BasicDBObject();
        dbo.put("_id", "a natural key");
        fakeEntities.insert(dbo);

        FakeEntityWithNaturalId entity = session.get("a natural key", FakeEntityWithNaturalId.class);

        Assert.assertThat(entity, notNullValue());
        Assert.assertThat(entity.getNaturalKey(), is("a natural key"));
    }

    @Test
    public void canSave() {
        FakeEntity entity = new FakeEntity("value");

        session.save(entity);

        Assert.assertThat(entities.getObjects().size(), is(1));
        DBObject dbo = entities.getObjects().get(0);
        Assert.assertThat(dbo.get("value"), is((Object) "value"));
    }

    @Test
    public void cantSaveSomethingWichIsNotAnEntity() {
        exception.expect(MongoLinkError.class);
        exception.expectMessage("Comment is not an entity");
        session.save(new Comment());
    }

    @Test
    public void canUpdate() {
        createEntity("4d53b7118653a70549fe1b78", "url de test");
        FakeEntity entity = session.get("4d53b7118653a70549fe1b78", FakeEntity.class);
        entity.setValue("un test de plus");

        session.update(entity);

        assertThat(entities.getObjects().get(0).get("value"), is((Object) "un test de plus"));
    }

    @Test
    public void cantUpdateSomethingWichIsNotAnEntity() {
        exception.expect(MongoLinkError.class);
        exception.expectMessage("Comment is not an entity");
        session.update(new Comment());
    }


    @Test
    public void canAutomaticalyUpdate() {
        createEntity("4d53b7118653a70549fe1b78", "url de test");
        session.start();
        FakeEntity fakeEntity = session.get("4d53b7118653a70549fe1b78", FakeEntity.class);
        fakeEntity.setValue("some new and strange value");

        session.stop();

        DBObject dbObject = entities.getObjects().get(0);
        assertThat(dbObject.get("value"), is(((Object) "some new and strange value")));
    }

    @Test
    public void returnNullIfNotFound() {
        FakeEntityWithNaturalId entity = session.get("a natural key", FakeEntityWithNaturalId.class);

        Assert.assertThat(entity, nullValue());
    }

    @Test
    public void canUpdateJustSavedEntityWithNaturalId() {
        FakeEntityWithNaturalId entity = new FakeEntityWithNaturalId("natural key");
        session.save(entity);
        entity.setValue("a value");

        session.stop();

        assertThat(fakeEntities.getObjects().get(0).get("value"), is((Object) "a value"));
    }

    @Test
    public void canUpdateJustSavedEntityWithAutoId() {
        FakeEntity entity = new FakeEntity("this is a value");
        session.save(entity);
        entity.setValue("a value");

        session.stop();

        assertThat(entities.getObjects().get(0).get("value"), is((Object) "a value"));
    }

    @Test
    public void savingSetIdForAutoId() {
        FakeEntity entity = new FakeEntity("a value");

        session.save(entity);

        assertThat(entity.getId(), notNullValue());
    }

    @Test
    public void canGetCriteria() {
        final Criteria criteria = session.createCriteria(FakeEntity.class);

        assertThat(criteria, notNullValue());
        assertEquals(criteria.getEntityType(), FakeEntity.class);
    }

    @Test
    public void canGetByCriteria() {
        session.save(new FakeEntity("this is a value"));
        session.save(new FakeEntity("this is a value"));
        final Criteria criteria = session.createCriteria(FakeEntity.class);

        List<FakeEntity> list =  criteria.list();

        assertThat(list.size(), is(2));
    }

    @Test
    @Ignore
    public void returnSameInstance() {
         DBObject dbo = new BasicDBObject();
        dbo.put("_id", "a natural key");
        fakeEntities.insert(dbo);

        FakeEntityWithNaturalId first = session.get("a natural key", FakeEntityWithNaturalId.class);
        FakeEntityWithNaturalId second = session.get("a natural key", FakeEntityWithNaturalId.class);

        assertThat(first, sameInstance(second));
    }

    private void createEntity(String id, String url) {
        DBObject dbo = new BasicDBObject();
        dbo.put("value", url);
        dbo.put("_id", new ObjectId(id));
        entities.insert(dbo);
    }

    private FakeDBCollection entities;
    private FakeDB db;
    private MongoSession session;
    private FakeDBCollection fakeEntities;

}
