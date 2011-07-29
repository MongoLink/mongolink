package fr.bodysplash.mongolink.domain.converter;

import org.joda.time.DateTime;

public class DateTimeConverter extends Converter {

    @Override
    public Object toDbValue(final Object value) {
        return ((DateTime) value).getMillis();
    }

    @Override
    public Object fromDbValue(final Object value) {
        return new DateTime((Long) value);
    }
}
