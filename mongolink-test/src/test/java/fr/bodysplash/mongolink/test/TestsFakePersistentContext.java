package fr.bodysplash.mongolink.test;

import com.mongodb.FakeDB;
import fr.bodysplash.mongolink.MongoSession;
import fr.bodysplash.mongolink.domain.criteria.Criteria;
import fr.bodysplash.mongolink.domain.criteria.Restriction;
import fr.bodysplash.mongolink.domain.criteria.Restrictions;
import fr.bodysplash.mongolink.test.criteria.FakeRestrictionEq;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class TestsFakePersistentContext {

    @Before
    public void setUp() throws Throwable {
        context = new FakePersistentContext("fr.bodysplash.mongolink.test");
        context.before();
    }

    @Test
    public void canGetSession() {
        final MongoSession session = context.getSession();

        assertThat(session, notNullValue());
    }

    @Test
    public void doesUseAFakeDb() {
        final MongoSession session = context.getSession();

        assertThat(session.getDb(), instanceOf(FakeDB.class));
    }

    @Test
    public void doesUseFakeCriteria() {
        final MongoSession session = context.getSession();

        final Criteria criteria = session.createCriteria(String.class);

        assertThat(criteria, instanceOf(FakeCriteria.class));
    }

    @Test
    public void restrictionsAreFake() {
        final Restriction restriction = Restrictions.eq("f", 3);

        assertThat(restriction, instanceOf(FakeRestrictionEq.class));
    }

    private FakePersistentContext context;
}
