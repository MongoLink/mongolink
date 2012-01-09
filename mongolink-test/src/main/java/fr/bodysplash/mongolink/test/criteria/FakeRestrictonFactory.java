package fr.bodysplash.mongolink.test.criteria;

import fr.bodysplash.mongolink.domain.criteria.Restriction;
import fr.bodysplash.mongolink.domain.criteria.RestrictionBetween;
import fr.bodysplash.mongolink.domain.criteria.RestrictionFactory;

public class FakeRestrictonFactory extends RestrictionFactory {

    @Override
    public Restriction getEquals(String field, Object value) {
        return new FakeRestrictionEquals(field, value);
    }

    @Override
    public RestrictionBetween getBetween(String field, Object start, Object end) {
        return new FakeRestrictionBetween(field, start, end);
    }

    @Override
    public Restriction getNotEquals(final String field, final Object value) {
        return new FakeRestrictionNotEquals(field, value);
    }
}
