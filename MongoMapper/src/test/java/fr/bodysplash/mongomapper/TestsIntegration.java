package fr.bodysplash.mongomapper;


import com.mongodb.DB;
import com.mongodb.Mongo;
import fr.bodysplash.mongomapper.mapper.ContextBuilder;
import fr.bodysplash.mongomapper.mapper.IdGeneration;
import fr.bodysplash.mongomapper.mapper.MapperContext;
import fr.bodysplash.mongomapper.mapper.Mapping;
import fr.bodysplash.mongomapper.test.FakeEntity;
import fr.bodysplash.mongomapper.test.FakeEntityWithNaturalId;
import fr.bodysplash.mongomapper.test.TestFactory;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.net.UnknownHostException;

public class TestsIntegration {

    private MongoSession mongoSession;

    @Before
    public void before() throws UnknownHostException {
        Mongo mongo = new Mongo();
        DB db = mongo.getDB("test");
        ContextBuilder builder = new ContextBuilder();
        Mapping<FakeEntity> mapping = builder.newMapping(FakeEntity.class);
        mapping.id().getId();
        mapping.property().getValue();
        Mapping<FakeEntityWithNaturalId> mappingNatural = builder.newMapping(FakeEntityWithNaturalId.class);
        mappingNatural.id(IdGeneration.Natural).getNaturalKey();
        MapperContext context = builder.createContext();
        mongoSession = new MongoSession(db);
        mongoSession.setMappingContext(context);
    }

    @Test
    @Ignore
    public void canGetById() {
        FakeEntity entityFound = mongoSession.get("4d53bf7c75b3a70563d9d0dd", FakeEntity.class);

        Assert.assertThat(entityFound, Matchers.notNullValue());
        Assert.assertThat(entityFound.getId(), Matchers.is("4d53bf7c75b3a70563d9d0dd"));
        Assert.assertThat(entityFound.getValue(), Matchers.is("value a62af4f1-553c-4bde-b36d-40abfec572a2"));
    }

    @Test
    @Ignore
    public void canGetByNaturalId() {
        FakeEntityWithNaturalId test = new FakeEntityWithNaturalId("clef naturel");
        mongoSession.save(test);

        FakeEntityWithNaturalId fakeEntityWithNaturalId = mongoSession.get("clef naturel", FakeEntityWithNaturalId.class);

        Assert.assertThat(fakeEntityWithNaturalId, Matchers.notNullValue());
        Assert.assertThat(fakeEntityWithNaturalId.getNaturalKey(), Matchers.is("clef naturel"));
    }


    @Test
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
