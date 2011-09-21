package fr.bodysplash.mongolink.domain.criteria;

public class Restrictions {

    public static Restriction eq(String field, Object value) {
        return factory.getEq(field, value);
    }

    public static Restriction between(String field, Object start, Object end) {
        return factory.getBetween(field, start, end);
    }

    public static void setFactory(RestrictionFactory factory) {
        Restrictions.factory = factory;
    }

    private static RestrictionFactory factory = new RestrictionFactory();

}
