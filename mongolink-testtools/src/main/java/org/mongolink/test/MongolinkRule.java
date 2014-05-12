package org.mongolink.test;

import org.junit.rules.ExternalResource;
import org.mongolink.*;
import org.mongolink.domain.mapper.ContextBuilder;

public class MongolinkRule extends ExternalResource {

    public static MongolinkRule withPackage(String... packagesToScan) {
        MongolinkRule result = new MongolinkRule();
        ContextBuilder contextBuilder = new ContextBuilder(packagesToScan);
        sesionManager = MongoSessionManager.create(contextBuilder, Settings.defaultInstance().withDbFactory(FongoDbFactory.class));
        return result;
    }

    private MongolinkRule() {

    }

    @Override
    public void before() throws Throwable {
        session = sesionManager.createSession();
        session.start();
    }

    @Override
    public void after() {
        session.stop();
        FongoDbFactory.clean();
    }

    public void cleanSession() {
        session.flush();
        session.clear();
    }

    public MongoSession getCurrentSession() {
        return session;
    }

    private static MongoSessionManager sesionManager;
    private MongoSession session;
}
