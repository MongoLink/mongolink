package fr.bodysplash.mongolink.utils;

import fr.bodysplash.mongolink.DbFactory;
import fr.bodysplash.mongolink.MongoSession;
import fr.bodysplash.mongolink.Settings;
import fr.bodysplash.mongolink.criteria.Criteria;
import fr.bodysplash.mongolink.criteria.CriteriaFactory;
import fr.bodysplash.mongolink.test.factory.FakeDbFactory;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class TestsSettings {

    @Test
    public void canCreateDbFactory() {
        Settings settings = Settings.defaultInstance().withPort(1234).withHost("localhost").withDbFactory(FakeDbFactory.class);

        FakeDbFactory dbFactory = (FakeDbFactory) settings.createDbFactory();

        assertThat(dbFactory.host, is("localhost"));
        assertThat(dbFactory.port, is(1234));
    }

    @Test
    public void canCreateDefaultSettings() {
        Settings settings = Settings.defaultInstance();

        assertThat(settings, notNullValue());
        DbFactory dbFactory = settings.createDbFactory();
        assertThat(dbFactory, notNullValue());
        assertThat(dbFactory, not(instanceOf(FakeDbFactory.class)));
        assertThat(dbFactory.getPort(), is(27017));
        assertThat(dbFactory.getHost(), is("127.0.0.1"));
        assertThat(settings.getDbName(), is("test"));
    }

    @Test
    public void canDefineDbName() {
        Settings settings = Settings.defaultInstance().withDbName("pouette");

        assertThat(settings.getDbName(), is("pouette"));
    }

    @Test
    public void canDefineCriteriaFactory() {
        final Settings settings = Settings.defaultInstance().withCriteriaFactory(DummyCriteriaFactory.class);

        final CriteriaFactory criteriaFactory = settings.getCriteriaFactory();

        assertThat(criteriaFactory, notNullValue());
        assertThat(criteriaFactory, instanceOf(DummyCriteriaFactory.class));
    }

    public static class DummyCriteriaFactory extends CriteriaFactory {

        @Override
        public Criteria create(Class<?> type, MongoSession mongoSession) {
            return new DummyCriteria();
        }
    }

    public static class DummyCriteria extends Criteria {
        public DummyCriteria() {
            super(null, null);
        }
    }
}
