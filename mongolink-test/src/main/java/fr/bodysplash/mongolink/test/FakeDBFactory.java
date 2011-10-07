package fr.bodysplash.mongolink.test;

import com.google.common.collect.Maps;
import com.mongodb.FakeDB;
import fr.bodysplash.mongolink.DbFactory;

import java.util.Map;

public class FakeDBFactory extends DbFactory {

    @Override
    public FakeDB get(final String dbName) {
        if (!dbs.containsKey(dbName)) {
            dbs.put(dbName, new FakeDB());
        }
        return dbs.get(dbName);
    }

    private Map<String, FakeDB> dbs = Maps.newHashMap();
}
