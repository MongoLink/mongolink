package fr.bodysplash.mongomapper;


import com.mongodb.*;
import fr.bodysplash.mongomapper.test.FakeEntity;
import fr.bodysplash.mongomapper.test.FakeEntityWithNaturalId;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class MongoSessionTests {

    private FakeDBCollection entities;
    private FakeDB db;
    private MongoSession session;
    private FakeDBCollection fakeEntities;

    @Before
    public void before() {
        Mongo mongo = mock(Mongo.class);
        db = spy(new FakeDB(mongo, "test"));
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

        InOrder inorder = inOrder(db);
        inorder.verify(db).requestStart();
        inorder.verify(db).requestDone();
    }


    @Test
    public void canGetByAutoId() {
        createEntity("4d53b7118653a70549fe1b78", "plop");
        createEntity("4d53b7118653a70549fe1b78", "plap");


        FakeEntity entity = session.get("4d53b7118653a70549fe1b78", FakeEntity.class);

        assertThat(entity, notNullValue());
        assertThat(entity.getValue(), is("plop"));
    }

    @Test
    public void canGetByNaturalId() {
        DBObject dbo = new BasicDBObject();
        dbo.put("_id", "a natural key");
        fakeEntities.insert(dbo);

        FakeEntityWithNaturalId entity = session.get("a natural key", FakeEntityWithNaturalId.class);

        assertThat(entity, notNullValue());
        assertThat(entity.getNaturalKey(), is("a natural key"));
    }

    @Test
    public void canSave() {
        FakeEntity entity = new FakeEntity("value");

        session.save(entity);

        assertThat(entities.getObjects().size(), is(1));
        DBObject dbo = entities.getObjects().get(0);
        assertThat(dbo.get("value"), is((Object) "value"));
    }

    @Test
    public void canUpdate() {
        createEntity("4d53b7118653a70549fe1b78", "url de test");
        FakeEntity entity = session.get("4d53b7118653a70549fe1b78", FakeEntity.class);
        entity.setValue("un test de plus");

        session.update(entity);

        assertThat(entities.getObjects().get(0).get("value"), is((Object) "un test de plus"));
    }

    private void createEntity(String id, String url) {
        DBObject dbo = new BasicDBObject();
        dbo.put("value", url);
        dbo.put("_id", new ObjectId(id));
        entities.insert(dbo);
    }

}
