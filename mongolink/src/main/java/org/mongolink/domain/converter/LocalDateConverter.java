package org.mongolink.domain.converter;

import org.joda.time.LocalDate;

public class LocalDateConverter extends Converter {
    @Override
    public Object toDbValue(Object value) {
        return ((LocalDate) value).toDateTimeAtStartOfDay().getMillis();
    }

    @Override
    public Object fromDbValue(Object value) {
        return new LocalDate(value);
    }
}
