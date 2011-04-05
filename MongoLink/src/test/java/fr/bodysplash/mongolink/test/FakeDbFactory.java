package fr.bodysplash.mongolink.test;

import com.google.common.collect.Maps;
import com.mongodb.DBAddress;
import com.mongodb.FakeDB;
import fr.bodysplash.mongolink.DbFactory;

import java.util.Map;

public class FakeDbFactory extends DbFactory {

    private final Map<String, FakeDB> dbs = Maps.newHashMap();

    @Override
    public FakeDB get(DBAddress db) {
        if (!dbs.containsKey(db.getDBName())) {
            dbs.put(db.getDBName(), new FakeDB());
        }
        return dbs.get(db.getDBName());
    }
}
