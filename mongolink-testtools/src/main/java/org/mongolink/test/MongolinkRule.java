package org.mongolink.test;

import com.github.fakemongo.Fongo;
import com.mongodb.*;
import com.mongodb.client.MongoDatabase;
import org.junit.rules.ExternalResource;
import org.mongolink.*;
import org.mongolink.domain.mapper.ContextBuilder;

public class MongolinkRule extends ExternalResource {

    public static MongolinkRule withPackage(String... packagesToScan) {
        MongolinkRule result = new MongolinkRule();
        ContextBuilder contextBuilder = new ContextBuilder(packagesToScan);
        sesionManager = MongoSessionManager.create(contextBuilder, Settings.defaultInstance().withDatabase(fongo.getDatabase("test")));
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
        cleanFongo();
    }

    public MongoDatabase getDatabase() {
        return fongo.getDatabase("test");
    }

    public DB getDB() {
        return fongo.getDB("test");
    }


    private void cleanFongo() {
        for (String databaseName : fongo.getDatabaseNames()) {
            DB db = fongo.getDB(databaseName);
            for (String collectionName : db.getCollectionNames()) {
                db.getCollection(collectionName).remove(new BasicDBObject());
            }

        }
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
    private static final Fongo fongo = new Fongo("Test Server");

}
