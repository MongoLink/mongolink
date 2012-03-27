package org.mongolink.domain.criteria;

import com.mongodb.*;

public class RestrictionGreaterThanOrEqualTo extends Restriction {

    public RestrictionGreaterThanOrEqualTo(final String field, final Object value) {
        super(field);
        this.value = value;
    }

    @Override
    public void apply(final DBObject query) {
        final BasicDBObject object = new BasicDBObject();
        object.put("$gte", getDBValue(value));
        query.put(getField(), object);
    }

    protected final Object value;
}
