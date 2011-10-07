package fr.bodysplash.mongolink.test.criteria;

import com.mongodb.DBObject;
import fr.bodysplash.mongolink.domain.criteria.RestrictionEq;

public class FakeRestrictionEq extends RestrictionEq implements FakeRestriction {

    public FakeRestrictionEq(String field, Object value) {
        super(field, value);
        this.value = value;
    }

    @Override
    public boolean isSatisfiedBy(DBObject entity) {
        return entity.get(getField()).equals(getDBValue(value));
    }

    private Object value;
}
