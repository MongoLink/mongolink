package fr.bodysplash.mongomapper;


import com.mongodb.DB;
import com.mongodb.Mongo;
import fr.bodysplash.mongomapper.test.Entity;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.net.UnknownHostException;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class IntegrationTests {

    private MongoSession mongoSession;

    @Before
    public void before() throws UnknownHostException {
        Mongo mongo = new Mongo();
        DB db = mongo.getDB("test");
        ContextBuilder builder = new ContextBuilder();
        Mapping<Entity> mapping = builder.newMapping(Entity.class);
        mapping.id().getId();
        mapping.property().getValue();
        MappingContext context = builder.createContext();
        mongoSession = new MongoSession(db);
        mongoSession.setMappingContext(context);
    }

    @Test
    @Ignore
    public void canGetById() {
        Entity entityFound = mongoSession.get("4d53bf7c75b3a70563d9d0dd", Entity.class);

        assertThat(entityFound, notNullValue());
        assertThat(entityFound.getId(), is("4d53bf7c75b3a70563d9d0dd"));
        assertThat(entityFound.getValue(), is("value a62af4f1-553c-4bde-b36d-40abfec572a2"));
    }

    private String createEntity(String value) {
        Entity entity = new Entity("value");
        entity.setValue(value);
        mongoSession.save(entity);
        return entity.getId();
    }
}
