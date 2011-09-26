package fr.bodysplash.mongolink.test.factory;

import com.google.common.collect.Maps;
import com.mongodb.FakeDB;
import fr.bodysplash.mongolink.DbFactory;

import java.util.Map;

public class FakeDbFactory extends DbFactory {

    @Override
    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public FakeDB get(String dbName) {
        if (!dbs.containsKey(dbName)) {
            dbs.put(dbName, new FakeDB());
        }
        return dbs.get(dbName);
    }

    private final Map<String, FakeDB> dbs = Maps.newHashMap();
    public String host;
    public int port;
}
