package org.mongolink.domain.criteria;

import com.mongodb.*;
import org.bson.Document;

public class RestrictionEqualsToRegex extends Restriction {

    public RestrictionEqualsToRegex(final String field, final Object value) {
        super(field);
        this.value = value;
    }

    @Override
    public void apply(final Document query) {
        final BasicDBObject object = new BasicDBObject();
        object.put("$regex", getDBValue(value));
        query.put(getField(), object);
    }

    protected final Object value;
}
