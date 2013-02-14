package org.mongolink.test;

import org.junit.rules.ExternalResource;
import org.mongolink.MongoSession;
import org.mongolink.MongoSessionManager;
import org.mongolink.Settings;
import org.mongolink.domain.mapper.ContextBuilder;

public class MongolinkRule extends ExternalResource {

    public static MongolinkRule withPackage(String packageToScan) {
        MongolinkRule result = new MongolinkRule();
        ContextBuilder contextBuilder = new ContextBuilder(packageToScan);
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
