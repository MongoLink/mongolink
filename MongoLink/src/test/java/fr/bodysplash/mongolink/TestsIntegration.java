package fr.bodysplash.mongolink;


import com.mongodb.DB;
import com.mongodb.Mongo;
import fr.bodysplash.mongolink.mapper.ContextBuilder;
import fr.bodysplash.mongolink.mapper.MapperContext;
import fr.bodysplash.mongolink.test.FakeEntity;
import fr.bodysplash.mongolink.test.FakeEntityWithNaturalId;
import fr.bodysplash.mongolink.test.TestFactory;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.net.UnknownHostException;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class TestsIntegration {

    private MongoSession mongoSession;

    @Before
    public void before() throws UnknownHostException {
        Mongo mongo = new Mongo();
        DB db = mongo.getDB("test");
        ContextBuilder builder = new ContextBuilder("fr.bodysplash.mongolink.test");
        MapperContext context = builder.createContext();
        mongoSession = new MongoSession(db);
        mongoSession.setMappingContext(context);
    }

    @Test
    @Ignore
    public void canGetById() {
        FakeEntity entityFound = mongoSession.get("4d53bf7c75b3a70563d9d0dd", FakeEntity.class);

        assertThat(entityFound, notNullValue());
        assertThat(entityFound.getId(), is("4d53bf7c75b3a70563d9d0dd"));
        assertThat(entityFound.getValue(), is("a new value"));
    }

    @Test
    @Ignore
    public void canGetByNaturalId() {
        FakeEntityWithNaturalId test = new FakeEntityWithNaturalId("clef naturel");
        mongoSession.save(test);

        FakeEntityWithNaturalId fakeEntityWithNaturalId = mongoSession.get("clef naturel", FakeEntityWithNaturalId.class);

        assertThat(fakeEntityWithNaturalId, notNullValue());
        assertThat(fakeEntityWithNaturalId.getNaturalKey(), is("clef naturel"));
    }


    @Test
    @Ignore
    public void canUserSessionManager() {
        ContextBuilder contextBuilder = TestFactory.contextBuilder().withFakeEntity();
        MongoSessionManager manager = MongoSessionManager.create(contextBuilder, "test");
        MongoSession session = manager.createSession();
        session.save(new FakeEntity("a new value"));
    }

    private String createEntity(String value) {
        FakeEntity entity = new FakeEntity("value");
        entity.setValue(value);
        mongoSession.save(entity);
        return entity.getId();
    }
}
