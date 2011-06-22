package fr.bodysplash.mongolink.test.criteria;

import com.mongodb.DBObject;
import fr.bodysplash.mongolink.criteria.RestrictionBetween;

public class FakeRestrictionBetween extends RestrictionBetween implements FakeRestriction{

    public FakeRestrictionBetween(String field, Object start, Object end) {
        super(field, start, end);
        this.start = start;
        this.end = end;
    }

    @Override
    public boolean isSatisfiedBy(DBObject entity) {
        final Comparable value = (Comparable) entity.get(getField());
        return value.compareTo(getDBValue(start)) >= 0 && value.compareTo(getDBValue(end)) < 0;
    }

    private Object start;
    private Object end;
}
