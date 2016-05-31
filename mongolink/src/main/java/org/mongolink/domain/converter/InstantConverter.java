package org.mongolink.domain.converter;


import java.time.Instant;

public class InstantConverter extends Converter {
    @Override
    public Object toDbValue(Object value) {
        return value.toString();
    }

    @Override
    protected Object fromDbValueNotNull(Object value) {
        return Instant.parse((String) value);
    }
}
