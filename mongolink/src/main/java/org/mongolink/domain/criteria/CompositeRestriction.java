package org.mongolink.domain.criteria;

import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;

public abstract class CompositeRestriction extends Restriction{

    public CompositeRestriction(final String field) {
        super(field);
    }

    public CompositeRestriction equals(final String field, final Object value) {
        restrictions.add(Restrictions.equals(field, value));
        return this;
    }

    protected List<Restriction> getRestrictions() {
        return Collections.unmodifiableList(restrictions);
    }

    private List<Restriction> restrictions = Lists.newArrayList();
}
