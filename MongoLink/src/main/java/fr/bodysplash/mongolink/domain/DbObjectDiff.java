package fr.bodysplash.mongolink.domain;


import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class DbObjectDiff {

    public DbObjectDiff(final BasicDBObject origin) {
        this.origin = origin;
    }

    public DBObject compareWith(DBObject target) {
        final BasicDBObject result = new BasicDBObject();

        final BasicDBObject set = generateSet(target);
        if (!set.isEmpty()) {
            result.append("$set", set);
        }
        return result;
    }

    private BasicDBObject generateSet(DBObject target) {
        BasicDBObject set = new BasicDBObject();
        for (String key : target.keySet()) {
            if (hasDifference(key, target)) {
                set.append(key, target.get(key));
            }
        }
        return set;
    }

    private boolean hasDifference(final String key, final DBObject target) {
        return !origin.get(key).equals(target.get(key));
    }

    private BasicDBObject origin;
}
