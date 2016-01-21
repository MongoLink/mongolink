package org.mongolink.domain.converter;

import org.joda.time.Interval;

public class IntervalConverter extends Converter {
    @Override
    public Object toDbValue(Object value) {
        return value.toString();
    }

    @Override
    protected Object fromDbValueNotNull(Object value) {
        return Interval.parse((String) value);
    }
}
