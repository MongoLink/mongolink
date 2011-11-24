package fr.bodysplash.mongolink;

import fr.bodysplash.mongolink.domain.UpdateStrategies;
import fr.bodysplash.mongolink.domain.mapper.*;
import fr.bodysplash.mongolink.domain.updateStategy.DiffStrategy;
import fr.bodysplash.mongolink.test.entity.*;
import fr.bodysplash.mongolink.test.factory.*;
import org.junit.*;

import java.net.UnknownHostException;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.*;

public class TestsMongoSessionManager {

    @Before
    public void before() {
        contextBuilder = TestFactory.contextBuilder().withFakeEntity();
        final Settings settings = Settings.defaultInstance().withDbFactory(FakeDbFactory.class).withDefaultUpdateStrategy(UpdateStrategies.DIFF);
        manager = MongoSessionManager.create(contextBuilder, settings);
    }

    @After
    public void after() throws UnknownHostException {
        manager.close();
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
    }

    @Test
    public void canGetCriteria() {
        MongoSession session = manager.createSession();

        assertThat(session.createCriteria(FakeEntity.class), notNullValue());
    }

    @Test
    public void canSave() {
        MongoSession session = manager.createSession();

        session.save(new FakeEntity("id"));

        assertThat(session.getDb().getCollection("fakeentity").count(), is(1L));
    }

    @Test
    public void canSetUpdateStrategy() {
        final MongoSession session = manager.createSession();

        assertThat(session.getUpdateStrategy(), instanceOf(DiffStrategy.class));
    }

    @Test
    @Ignore("trop mal codé pour le moment, je ne vais pas essayer de le tester je referai tout proprement")
    public void canSetCappedCollections() {
        final MapperContext mapperContext = manager.getMapperContext();
        final EntityMapper<FakeEntityWithCap> fakeEntityWithCapMapper = (EntityMapper<FakeEntityWithCap>) mapperContext.mapperFor(FakeEntityWithCap.class);
        final MongoSession session = manager.createSession();

        assertThat(session.getDb().getCollection(fakeEntityWithCapMapper.collectionName()).isCapped(), is(true));
        assertThat(fakeEntityWithCapMapper.getCappedSize(), is(1048076));
        assertThat(fakeEntityWithCapMapper.getCappedMax(), is(50));
    }

    private static ContextBuilder contextBuilder;
    private static MongoSessionManager manager;
}
