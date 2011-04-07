package fr.bodysplash.mongolink;

import com.mongodb.FakeDB;
import fr.bodysplash.mongolink.mapper.ContextBuilder;
import fr.bodysplash.mongolink.test.FakeDbFactory;
import fr.bodysplash.mongolink.test.FakeEntity;
import fr.bodysplash.mongolink.test.TestFactory;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

public class TestsMongoSessionManager {

    @Test
    public void canCreateFromContextBuilder() {
        ContextBuilder contextBuilder = new ContextBuilder("fr.bodysplash.mongolink.test");

        MongoSessionManager sm = MongoSessionManager.create(contextBuilder, Settings.defaultInstance());

        assertThat(sm, notNullValue());
        assertThat(sm.getMapperContext(), notNullValue());
        assertThat(sm.getMapperContext().mapperFor(FakeEntity.class), notNullValue());
    }

    @Test
    public void canCreateSession() {
        ContextBuilder contextBuilder = TestFactory.contextBuilder().withFakeEntity();
        MongoSessionManager sm = MongoSessionManager.create(contextBuilder, Settings.defaultInstance().withFactory(FakeDbFactory.class));


        MongoSession session = sm.createSession();

        assertThat(session, notNullValue());
        session.save(new FakeEntity("id"));
        FakeDB db = (FakeDB) session.getDb();
        assertThat(db.collections.get("fakeentity").getObjects().size(), is(1));
    }

}
