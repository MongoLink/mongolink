package fr.bodysplash.mongomapper;


import com.mongodb.*;
import fr.bodysplash.mongomapper.test.Entity;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class MongoSessionTests {

    private FakeDBCollection patates;
    private FakeDB db;
    private MongoSession session;

    @Before
    public void before() {
        Mongo mongo = mock(Mongo.class);
        db = spy(new FakeDB(mongo, "test"));
        patates = new FakeDBCollection(db, "entity");
        db.collections.put("entity", patates);
        ContextBuilder cb = new ContextBuilder();
        Mapping<Entity> mapping = cb.newMapping(Entity.class);
        mapping.property().getValue();
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
    public void canGetById() {
        createEntity("id", "url de test");
        createEntity("autreId", "autre url");


        Entity entity = session.get("id", Entity.class);

        assertThat(entity , notNullValue());
        assertThat(entity.getValue(), is("url de test"));
    }

    private void createEntity(String id, String url) {
        DBObject dbo = new BasicDBObject();
        dbo.put("value", url);
        dbo.put("_id", id);
        patates.insert(dbo);
    }

    @Test
    public void canSave() {
        Entity entity = new Entity("value");

        session.save(entity);

        assertThat(patates.getObjects().size(), is(1));
        DBObject dbo = patates.getObjects().get(0);
        assertThat(dbo.get("value"), is((Object)"value"));
    }
    
    
    
}
