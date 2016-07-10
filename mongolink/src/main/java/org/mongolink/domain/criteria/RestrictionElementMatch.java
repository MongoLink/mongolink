package org.mongolink.domain.criteria;

import org.bson.Document;

public class RestrictionElementMatch extends CompositeRestriction {

    public RestrictionElementMatch(final String field) {
        super(field);
    }

    @Override
    public void apply(final Document query) {
        query.put(getField(), new Document("$elemMatch", buildSubquery()));
    }

    private Document buildSubquery() {
        final Document subquery = new Document();
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
