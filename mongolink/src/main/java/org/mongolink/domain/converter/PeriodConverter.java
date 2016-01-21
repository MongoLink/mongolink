package org.mongolink.domain.converter;


import java.time.Period;

public class PeriodConverter extends Converter {
    @Override
    public Object toDbValue(Object value) {
        return value.toString();
    }

    @Override
    protected Object fromDbValueNotNull(Object value) {
        return Period.parse((String) value);
    }
}
