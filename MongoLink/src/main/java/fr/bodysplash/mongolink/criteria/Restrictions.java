package fr.bodysplash.mongolink.criteria;

public class Restrictions {

    public static Restriction eq(String field, Object value) {
       return new RestrictionEq(field, value);
    }

    public static Restriction between(String field, Object start, Object end) {
        return new RestrictionBetween(field, start, end);
    }
}
