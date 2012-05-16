package org.mongolink.domain.criteria;

import com.mongodb.*;

import java.util.*;

public class RestrictionIn extends Restriction {

    public RestrictionIn(String field, List<String> tokens) {
        super(field);
        this.tokens = tokens;
    }

    @Override
    public void apply(final DBObject query) {
        final BasicDBObject object = new BasicDBObject();
        final StringBuilder tokens = buildTokensList();
        object.put("$in", tokens.toString());
        query.put(getField(), object);
    }

    private StringBuilder buildTokensList() {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        final Iterator<String> iterator = tokens.iterator();
        while (iterator.hasNext()) {
            final String token = iterator.next();
            stringBuilder.append("\"");
            stringBuilder.append(token);
            stringBuilder.append("\"");
            if (iterator.hasNext()) {
                stringBuilder.append(",");
            }
        }
        stringBuilder.append("]");
        return stringBuilder;
    }

    private List<String> tokens;
}
