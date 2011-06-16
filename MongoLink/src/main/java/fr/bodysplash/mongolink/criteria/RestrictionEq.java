package fr.bodysplash.mongolink.criteria;

import com.mongodb.DBObject;

public class RestrictionEq extends Restriction {

    public RestrictionEq(String field, Object value) {
        super(field);
        this.value = value;
    }

    @Override
    public void apply(DBObject query) {
        query.put(getField(), getDBValue(value));
    }

    private Object value;
}
