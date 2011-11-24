package fr.bodysplash.mongolink.domain.criteria;

import com.mongodb.DBObject;

public class RestrictionEquals extends Restriction {

    public RestrictionEquals(String field, Object value) {
        super(field);
        this.value = value;
    }

    @Override
    public void apply(DBObject query) {
        query.put(getField(), getDBValue(value));
    }

    private Object value;
}
