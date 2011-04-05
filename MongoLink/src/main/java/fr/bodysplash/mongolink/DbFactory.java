package fr.bodysplash.mongolink;

import com.mongodb.DB;
import com.mongodb.DBAddress;
import com.mongodb.Mongo;

public class DbFactory {

    private Mongo mongo;

    public DB get(DBAddress db) {
        initializeMongo(db);
        return mongo.getDB(db.getDBName());
    }

    private void initializeMongo(DBAddress db) {
        if (mongo == null) {
            doInitializeMongo(db);
        }
    }

    private synchronized void doInitializeMongo(DBAddress db) {
        if (mongo == null) {
            mongo = new Mongo(db);
        }
    }

    public void close() {
        if (mongo != null) {
            mongo.close();
        }
    }
}
