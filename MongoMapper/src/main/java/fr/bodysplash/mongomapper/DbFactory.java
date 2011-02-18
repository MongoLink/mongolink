package fr.bodysplash.mongomapper;

import com.mongodb.DB;
import com.mongodb.Mongo;

import java.net.UnknownHostException;

public class DbFactory {

    private Mongo mongo;

    public DB get(String dbName) {
        initializeMongo();
        return mongo.getDB(dbName);
    }

    private void initializeMongo() {
        if (mongo == null) {
            doInitializeMongo();
        }
    }

    private synchronized void doInitializeMongo() {
        try {
            if (mongo == null) {
                mongo = new Mongo();
            }
        } catch (UnknownHostException e) {
            throw new MongoMapperError("Can't initialize mongo", e);
        }
    }

    public void close() {
        if(mongo != null) {
            mongo.close();
        }
    }
}
