package fr.bodysplash.mongolink.test.criteria;

import com.mongodb.DBObject;
import fr.bodysplash.mongolink.domain.criteria.RestrictionNotEquals;

public class FakeRestrictionNotEquals extends RestrictionNotEquals implements FakeRestriction {

    public FakeRestrictionNotEquals(final String field, final Object value) {
        super(field, value);
        this.value = value;
    }

    @Override
    public boolean isSatisfiedBy(final DBObject entity) {
        return !entity.get(getField()).equals(getDBValue(value));
    }

    private Object value;
}
