package org.mongolink.domain.criteria;

import org.bson.Document;

import java.util.List;

public class RestrictionIn extends Restriction {

    public RestrictionIn(String field, List<?> elements) {
        super(field);
        this.elements = elements;
    }

    @Override
    public void apply(final Document query) {
        final Document object = new Document();
        object.put("$in", elements);
        query.put(getField(), object);
    }

    private List<?> elements;
}
