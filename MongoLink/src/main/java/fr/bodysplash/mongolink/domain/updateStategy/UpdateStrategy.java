package fr.bodysplash.mongolink.domain.updateStategy;

import com.mongodb.*;

public abstract class UpdateStrategy {

    public abstract void update(DBObject initialValue, DBObject update, DBCollection collection);

    protected DBObject updateQuery(DBObject update) {
        DBObject query = new BasicDBObject();
        query.put("_id", update.get("_id"));
        return query;
    }
}
