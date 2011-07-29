package fr.bodysplash.mongolink.domain.criteria;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class RestrictionBetween extends Restriction {
    public RestrictionBetween(String field, Object start, Object end) {
        super(field);
        this.start = start;
        this.end = end;
    }

    @Override
    public void apply(DBObject query) {
        final BasicDBObject object = new BasicDBObject();
        object.put("$gte", getDBValue(start));
        object.put("$lt", getDBValue(end));
        query.put(getField(), object);
    }

    private Object start;
    private Object end;
}
