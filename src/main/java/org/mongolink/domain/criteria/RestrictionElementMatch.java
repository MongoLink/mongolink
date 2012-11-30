package org.mongolink.domain.criteria;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class RestrictionElementMatch extends NestedRestriction {

    public RestrictionElementMatch(final String field) {
        super(field);
    }

    @Override
    public void apply(final DBObject query) {
        query.put(getField(), new BasicDBObject("$elementMatch", buildSubquery()));
    }

    private BasicDBObject buildSubquery() {
        final BasicDBObject subquery = new BasicDBObject();
        for (Restriction restriction : getRestrictions()) {
            restriction.apply(subquery);
        }
        return subquery;
    }
}
