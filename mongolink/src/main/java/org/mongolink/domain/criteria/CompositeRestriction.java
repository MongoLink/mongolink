package org.mongolink.domain.criteria;

import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;

public abstract class CompositeRestriction extends Restriction{

    public CompositeRestriction(final String field) {
        super(field);
    }

    protected List<Restriction> getRestrictions() {
        return Collections.unmodifiableList(restrictions);
    }

    public CompositeRestriction with(Restriction restriction) {
        restrictions.add(restriction);
        return this;
    }

    private List<Restriction> restrictions = Lists.newArrayList();
}
