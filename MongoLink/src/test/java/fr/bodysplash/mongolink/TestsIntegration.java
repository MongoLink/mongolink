package fr.bodysplash.mongolink;


import com.google.common.collect.Lists;
import com.mongodb.*;
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

@Ignore
public class TestsIntegration {

    @Before
    public void before() throws UnknownHostException {
        Mongo mongo = new Mongo();
        db = mongo.getDB("test");
        ContextBuilder builder = new ContextBuilder("fr.bodysplash.mongolink.test");
        MapperContext context = builder.createContext();
        mongoSession = new MongoSession(db);
        mongoSession.setMappingContext(context);
    }

    @Test
    public void canGetById() {
        FakeEntity entityFound = mongoSession.get("4d9d9b5e36a9a4265ea9ecbe", FakeEntity.class);

        assertThat(entityFound, notNullValue());
        assertThat(entityFound.getId(), is("4d9d9b5e36a9a4265ea9ecbe"));
        assertThat(entityFound.getValue(), is("a new value"));
    }

    @Test
    public void canGetByNaturalId() {
        FakeEntityWithNaturalId test = new FakeEntityWithNaturalId("clef naturel");
        mongoSession.save(test);

        FakeEntityWithNaturalId fakeEntityWithNaturalId = mongoSession.get("clef naturel", FakeEntityWithNaturalId.class);

        assertThat(fakeEntityWithNaturalId, notNullValue());
        assertThat(fakeEntityWithNaturalId.getNaturalKey(), is("clef naturel"));
    }

    @Test
    public void canUserSessionManager() {
        ContextBuilder contextBuilder = TestFactory.contextBuilder().withFakeEntity();
        MongoSessionManager manager = MongoSessionManager.create(contextBuilder, Settings.defaultInstance());
        MongoSession session = manager.createSession();
        session.save(new FakeEntity("a new value"));
    }


    @Test
    public void insertingSetId() {
        DBCollection testid = db.getCollection("testid");
        BasicDBObject dbo = new BasicDBObject();

        testid.insert(Lists.<DBObject>newArrayList(dbo));

        assertThat(dbo.get("_id"), notNullValue());
    }

    private String createEntity(String value) {
        FakeEntity entity = new FakeEntity("value");
        entity.setValue(value);
        mongoSession.save(entity);
        return entity.getId();
    }

    private MongoSession mongoSession;
    private DB db;
}
