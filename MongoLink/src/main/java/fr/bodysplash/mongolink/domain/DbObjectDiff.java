package fr.bodysplash.mongolink.domain;


import com.google.common.collect.Iterables;
import com.mongodb.*;

public class DbObjectDiff {

    public DbObjectDiff(final BasicDBObject origin) {
        this.origin = origin;
    }

    public DBObject compareWith(DBObject target) {
        final BasicDBObject result = new BasicDBObject();
        final String key = Iterables.get(origin.keySet(), 0);
        if (hasDifference(key, target)) {
            final BasicDBObject value = diffFor(key, target);
            result.append("$set", value);
        }
        return result;
    }

    private boolean hasDifference(final String key, final DBObject target) {
        return !origin.get(key).equals(target.get(key));
    }

    private BasicDBObject diffFor(final String key, final DBObject target) {
        final BasicDBObject result11 = new BasicDBObject();
        result11.append(key, target.get(key));
        return result11;
    }

    private BasicDBObject origin;
}
