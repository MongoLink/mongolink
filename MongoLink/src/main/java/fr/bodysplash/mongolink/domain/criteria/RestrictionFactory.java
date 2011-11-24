package fr.bodysplash.mongolink.domain.criteria;

public class RestrictionFactory {

    public RestrictionBetween getBetween(String field, Object start, Object end) {
        return new RestrictionBetween(field, start, end);
    }

    public Restriction getEquals(String field, Object value) {
        return new RestrictionEquals(field, value);
    }

    public Restriction getNotEquals(String field, Object value) {
        return new RestrictionNotEquals(field, value);
    }
}
