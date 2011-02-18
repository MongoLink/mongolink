package fr.bodysplash.mongomapper;

import fr.bodysplash.mongomapper.test.FakeDbFactory;
import fr.bodysplash.mongomapper.mapper.ContextBuilder;
import fr.bodysplash.mongomapper.test.FakeEntity;
import fr.bodysplash.mongomapper.test.TestFactory;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class TestsMongoSessionManager {

    @Test
    public void canCreateFromContextBuilder() {
        ContextBuilder contextBuilder = new ContextBuilder();
        contextBuilder.newMapping(FakeEntity.class);

        MongoSessionManager sm = MongoSessionManager.create(contextBuilder, "freecomment");

        assertThat(sm, notNullValue());
        assertThat(sm.getMapperContext(), notNullValue());
        assertThat(sm.getMapperContext().mapperFor(FakeEntity.class), notNullValue());
    }

    @Test
    public void canCreateSession() {
        FakeDbFactory fakeDbFactory = new FakeDbFactory();
        ContextBuilder contextBuilder = TestFactory.contextBuilder().withFakeEntity();
        MongoSessionManager sm = MongoSessionManager.create(contextBuilder, "freecomment");
        sm.setDbFactory(fakeDbFactory);

        MongoSession session = sm.createSession();

        assertThat(session, notNullValue());
        session.save(new FakeEntity("id"));
        assertThat(fakeDbFactory.get("freecomment").collections.get("fakeentity").getObjects().size(), is(1));
    }

    @Test
    public void canClose() {
        DbFactory fakeDbFactory = mock(DbFactory.class);
        ContextBuilder contextBuilder = TestFactory.contextBuilder().withFakeEntity();
        MongoSessionManager sm = MongoSessionManager.create(contextBuilder, "freecomment");
        sm.setDbFactory(fakeDbFactory);

        sm.close();

        verify(fakeDbFactory).close();
    }
}
