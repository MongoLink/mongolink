package fr.bodysplash.mongolink.domain.updateStategy;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import fr.bodysplash.mongolink.domain.DbObjectDiff;
import org.apache.log4j.Logger;

public class DiffStrategy extends UpdateStrategy {
    
    @Override
    public void update(DBObject initialValue, DBObject update, DBCollection collection) {
        final DBObject diff = new DbObjectDiff(initialValue).compareWith(update);
        if (!diff.keySet().isEmpty()) {
            final DBObject q = updateQuery(initialValue);
            LOGGER.debug("Updating query:" + q +" values: " + diff);
            collection.update(q, diff);
        }
    }

    private static Logger LOGGER = Logger.getLogger(DiffStrategy.class);
}
