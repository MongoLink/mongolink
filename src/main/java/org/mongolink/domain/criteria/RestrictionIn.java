package org.mongolink.domain.criteria;

import com.mongodb.*;

import java.util.List;

public class RestrictionIn extends Restriction {

    public RestrictionIn(String field, List<String> tokens) {
        super(field);
        this.tokens = tokens;
    }

    @Override
    public void apply(final DBObject query) {
        final BasicDBObject object = new BasicDBObject();
        final BasicDBList values = new BasicDBList();
        for (String token : tokens) {
            values.add(token);
        }
        object.put("$in", values);
        query.put(getField(), object);
    }

    private List<String> tokens;
}
