package org.mongolink.domain.converter;

import org.joda.time.LocalDate;

public class LocalDateConverter extends Converter {
    @Override
    public Object toDbValue(Object value) {
        return ((LocalDate) value).toDateTimeAtStartOfDay().getMillis();
    }

    @Override
    protected Object fromDbValueNotNull(Object value) {
        return new LocalDate(value);
    }
}
