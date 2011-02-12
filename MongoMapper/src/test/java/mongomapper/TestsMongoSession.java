package mongomapper;


import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.FakeDB;
import com.mongodb.FakeDBCollection;
import mongomapper.test.FakeEntity;
import mongomapper.test.FakeEntityWithNaturalId;
import org.bson.types.ObjectId;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

public class TestsMongoSession {

    private FakeDBCollection entities;
    private FakeDB db;
    private MongoSession session;
    private FakeDBCollection fakeEntities;

    @Before
    public void before() {
        db = Mockito.spy(new FakeDB());
        entities = new FakeDBCollection(db, "entity");
        fakeEntities = new FakeDBCollection(db, "fakeentitywithnaturalid");
        db.collections.put("fakeentity", entities);
        db.collections.put("fakeentitywithnaturalid", fakeEntities);
        ContextBuilder cb = new ContextBuilder();
        Mapping<FakeEntity> mapping = cb.newMapping(FakeEntity.class);
        mapping.property().getValue();
        mapping.id().getId();
        Mapping<FakeEntityWithNaturalId> mappingNatural = cb.newMapping(FakeEntityWithNaturalId.class);
        mappingNatural.id(IdGeneration.Natural).getNaturalKey();
        MappingContext context = cb.createContext();
        session = new MongoSession(db);
        session.setMappingContext(context);
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
        Assert.assertThat(entity.getValue(), Matchers.is("plop"));
    }

    @Test
    public void canGetByNaturalId() {
        DBObject dbo = new BasicDBObject();
        dbo.put("_id", "a natural key");
        fakeEntities.insert(dbo);

        FakeEntityWithNaturalId entity = session.get("a natural key", FakeEntityWithNaturalId.class);

        Assert.assertThat(entity, Matchers.notNullValue());
        Assert.assertThat(entity.getNaturalKey(), Matchers.is("a natural key"));
    }

    @Test
    public void canSave() {
        FakeEntity entity = new FakeEntity("value");

        session.save(entity);

        Assert.assertThat(entities.getObjects().size(), Matchers.is(1));
        DBObject dbo = entities.getObjects().get(0);
        Assert.assertThat(dbo.get("value"), Matchers.is((Object) "value"));
    }

    @Test
    public void canUpdate() {
        createEntity("4d53b7118653a70549fe1b78", "url de test");
        FakeEntity entity = session.get("4d53b7118653a70549fe1b78", FakeEntity.class);
        entity.setValue("un test de plus");

        session.update(entity);

        Assert.assertThat(entities.getObjects().get(0).get("value"), Matchers.is((Object) "un test de plus"));
    }

    @Test
    public void canAutomaticalyUpdate() {

    }

    private void createEntity(String id, String url) {
        DBObject dbo = new BasicDBObject();
        dbo.put("value", url);
        dbo.put("_id", new ObjectId(id));
        entities.insert(dbo);
    }

}
