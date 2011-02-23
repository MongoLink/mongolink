package fr.bodysplash.mongomapper.test;

import com.google.common.collect.Maps;
import com.mongodb.FakeDB;
import fr.bodysplash.mongomapper.DbFactory;

import java.util.Map;

public class FakeDbFactory extends DbFactory {

    private final Map<String, FakeDB> dbs = Maps.newHashMap();


    @Override
    public FakeDB get(String dbName) {
        if (!dbs.containsKey(dbName)) {
            dbs.put(dbName, new FakeDB());
        }
        return dbs.get(dbName);
    }
}
