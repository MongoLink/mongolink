package fr.bodysplash.mongolink.domain.updateStategy;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class OverwriteStrategy extends UpdateStrategy {

    @Override
    public void update(DBObject initialValue, DBObject update, DBCollection collection) {
        DBObject query = new BasicDBObject();
        query.put("_id", update.get("_id"));
        collection.update(updateQuery(initialValue), update);
    }
}
