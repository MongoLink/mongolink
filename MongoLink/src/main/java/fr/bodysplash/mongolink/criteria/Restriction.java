package fr.bodysplash.mongolink.criteria;

import com.mongodb.DBObject;
import fr.bodysplash.mongolink.converter.Converter;

public abstract class Restriction {

    public Restriction(String field) {
        this.field = field;
    }

    public abstract void apply(DBObject query);

    protected String getField() {
        return field;
    }

    protected Object getDBValue(Object value) {
        final Converter converter = Converter.forType(value.getClass());
        return converter.toDbValue(value);
    }

    private String field;
}
