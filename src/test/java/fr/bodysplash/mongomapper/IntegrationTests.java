package fr.bodysplash.mongomapper;


import com.mongodb.DB;
import com.mongodb.Mongo;
import fr.bodysplash.mongomapper.test.FakeEntity;
import fr.bodysplash.mongomapper.test.FakeEntityWithNaturalId;
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
        Mapping<FakeEntity> mapping = builder.newMapping(FakeEntity.class);
        mapping.id().getId();
        mapping.property().getValue();
        Mapping<FakeEntityWithNaturalId> mappingNatural = builder.newMapping(FakeEntityWithNaturalId.class);
        mappingNatural.id(IdGeneration.Natural).getNaturalKey();
        MappingContext context = builder.createContext();
        mongoSession = new MongoSession(db);
        mongoSession.setMappingContext(context);
    }

    @Test
    @Ignore
    public void canGetById() {
        FakeEntity entityFound = mongoSession.get("4d53bf7c75b3a70563d9d0dd", FakeEntity.class);

        assertThat(entityFound, notNullValue());
        assertThat(entityFound.getId(), is("4d53bf7c75b3a70563d9d0dd"));
        assertThat(entityFound.getValue(), is("value a62af4f1-553c-4bde-b36d-40abfec572a2"));
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

    private String createEntity(String value) {
        FakeEntity entity = new FakeEntity("value");
        entity.setValue(value);
        mongoSession.save(entity);
        return entity.getId();
    }
}
