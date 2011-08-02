package fr.bodysplash.mongolink.domain.updateStategy;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import fr.bodysplash.mongolink.domain.DbObjectDiff;

public class DiffStrategy extends UpdateStrategy {
    
    @Override
    public void update(DBObject initialValue, DBObject update, DBCollection collection) {
        final DBObject diff = new DbObjectDiff(initialValue).compareWith(update);
        if (!diff.keySet().isEmpty()) {
            collection.update(updateQuery(initialValue), diff);
        }
    }

}
