package org.mongolink.domain.criteria;

import com.mongodb.*;

import java.util.List;

public class RestrictionIn extends Restriction {

    public RestrictionIn(String field, List<?> elements) {
        super(field);
        this.elements = elements;
    }

    @Override
    public void apply(final DBObject query) {
        final BasicDBObject object = new BasicDBObject();
        final BasicDBList values = new BasicDBList();
        for (Object token : elements) {
            values.add(token);
        }
        object.put("$in", values);
        query.put(getField(), object);
    }

    private List<?> elements;
}
