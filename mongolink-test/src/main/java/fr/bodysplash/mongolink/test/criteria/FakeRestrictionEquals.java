package fr.bodysplash.mongolink.test.criteria;

import com.mongodb.DBObject;
import fr.bodysplash.mongolink.domain.criteria.RestrictionEquals;

public class FakeRestrictionEquals extends RestrictionEquals implements FakeRestriction {

    public FakeRestrictionEquals(String field, Object value) {
        super(field, value);
        this.value = value;
    }

    @Override
    public boolean isSatisfiedBy(DBObject entity) {
        return entity.get(getField()).equals(getDBValue(value));
    }

    private Object value;
}
