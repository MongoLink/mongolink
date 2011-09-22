package fr.bodysplash.mongolink;

import com.mongodb.*;
import fr.bodysplash.mongolink.domain.UpdateStrategies;
import fr.bodysplash.mongolink.domain.mapper.*;
import fr.bodysplash.mongolink.domain.updateStategy.DiffStrategy;
import fr.bodysplash.mongolink.test.entity.*;
import fr.bodysplash.mongolink.test.factory.TestFactory;
import org.junit.*;

import java.net.UnknownHostException;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.*;

public class TestsMongoSessionManager {

    @Before
    public void before() {
        contextBuilder = TestFactory.contextBuilder().withFakeEntity();
        manager = MongoSessionManager.create(contextBuilder, Settings.defaultInstance().withDefaultUpdateStrategy(UpdateStrategies.DIFF));
    }

    @After
    public void after() throws UnknownHostException {
        manager.close();
        Mongo mongo = new Mongo();
        DB db = mongo.getDB("test");
        db.dropDatabase();
    }

    @Test
    public void canCreateFromContextBuilder() {
        assertThat(manager, notNullValue());
        assertThat(manager.getMapperContext(), notNullValue());
        assertThat(manager.getMapperContext().mapperFor(FakeEntity.class), notNullValue());
    }

    @Test
    public void canCreateSession() {
        MongoSession session = manager.createSession();

        assertThat(session, notNullValue());
        session.save(new FakeEntity("id"));

        assertThat(session.getDb().getCollection("fakeentity").count(), is(1L));
        assertThat(session.createCriteria(String.class), notNullValue());
    }

    @Test
    public void canSetUpdateStrategy() {
        final MongoSession session = manager.createSession();

        assertThat(session.getUpdateStrategy(), instanceOf(DiffStrategy.class));
    }

    @Test
    public void canSetCappedCollections() {
        final MapperContext mapperContext = manager.getMapperContext();
        final EntityMapper<FakeEntityWithCap> fakeEntityWithCapMapper = (EntityMapper<FakeEntityWithCap>) mapperContext.mapperFor(FakeEntityWithCap.class);
        final MongoSession session = manager.createSession();
        assertThat(session.getDb().getCollection(fakeEntityWithCapMapper.collectionName()).isCapped(), is(true));
    }

    private static ContextBuilder contextBuilder;
    private static MongoSessionManager manager;
}
