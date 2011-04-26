package fr.bodysplash.mongolink.converter;

import org.joda.time.DateTime;

public class DateTimeConverter extends Converter {

    @Override
    public Object toDbValue(final Object value) {
        return ((DateTime) value).getMillis();
    }

    @Override
    public Object fromDbValue(final Object value) {
        Long time = Long.parseLong(value.toString());
        DateTime dateTime = new DateTime(time);
        return dateTime;
    }
}
