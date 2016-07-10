package org.mongolink.domain.criteria;

import org.bson.Document;

public class RestrictionGreaterThanOrEqualTo extends Restriction {

    public RestrictionGreaterThanOrEqualTo(final String field, final Object value) {
        super(field);
        this.value = value;
    }

    @Override
    public void apply(final Document query) {
        final Document object = new Document();
        object.put("$gte", getDBValue(value));
        query.put(getField(), object);
    }

    protected final Object value;
}
