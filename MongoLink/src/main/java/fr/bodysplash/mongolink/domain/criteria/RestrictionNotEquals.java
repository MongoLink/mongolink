package fr.bodysplash.mongolink.domain.criteria;

import com.mongodb.*;

public class RestrictionNotEquals extends Restriction {

    public RestrictionNotEquals(final String field, final Object value) {
        super(field);
        this.value = value;
    }

    @Override
    public void apply(final DBObject query) {
        final BasicDBObject object = new BasicDBObject();
        object.put("$ne", getDBValue(value));
        query.put(getField(), object);
    }

    private Object value;
}
