package org.mongolink.domain.criteria;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class RestrictionElementMatch extends CompositeRestriction {

    public RestrictionElementMatch(final String field) {
        super(field);
    }

    @Override
    public void apply(final DBObject query) {
        query.put(getField(), new BasicDBObject("$elemMatch", buildSubquery()));
    }

    private BasicDBObject buildSubquery() {
        final BasicDBObject subquery = new BasicDBObject();
        for (Restriction restriction : getRestrictions()) {
            restriction.apply(subquery);
        }
        return subquery;
    }

    public RestrictionElementMatch equals(final String field, final Object value) {
        with(Restrictions.equals(field, value));
        return this;
    }


}
