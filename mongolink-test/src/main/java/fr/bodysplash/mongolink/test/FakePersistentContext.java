package fr.bodysplash.mongolink.test;

import fr.bodysplash.mongolink.*;
import fr.bodysplash.mongolink.domain.criteria.*;
import fr.bodysplash.mongolink.domain.mapper.ContextBuilder;
import fr.bodysplash.mongolink.test.criteria.FakeRestrictonFactory;
import org.junit.rules.ExternalResource;

public class FakePersistentContext extends ExternalResource {

    public FakePersistentContext(String packageToScan) {
        this.packageToScan = packageToScan;
    }

    @Override
    protected void before() throws Throwable {
        ContextBuilder context = new ContextBuilder(packageToScan);
        final MongoSessionManager manager = MongoSessionManager.create(context, Settings.defaultInstance()
                .withDbFactory(FakeDBFactory.class)
                .withCriteriaFactory(FakeCriteriaFactory.class));
        session = manager.createSession();
        Restrictions.setFactory(new FakeRestrictonFactory());
    }

    @Override
    protected void after() {
        Restrictions.setFactory(new RestrictionFactory());
    }

    public MongoSession getSession() {
        return session;
    }

    private MongoSession session;
    private String packageToScan;
}
