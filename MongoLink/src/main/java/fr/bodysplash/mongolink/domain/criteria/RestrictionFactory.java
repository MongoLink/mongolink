package fr.bodysplash.mongolink.domain.criteria;

public class RestrictionFactory {

    public RestrictionBetween getBetween(String field, Object start, Object end) {
        return new RestrictionBetween(field, start, end);
    }

    public Restriction getEq(String field, Object value) {
        return new RestrictionEq(field, value);
    }
}
