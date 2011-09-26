package fr.bodysplash.mongolink.domain.converter;


import org.joda.time.DateTime;
import org.junit.*;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class TestsDateTimeConverter {

    private DateTimeConverter converter;

    @Before
    public void setUp() throws Exception {
        converter = new DateTimeConverter();
    }

    @Test
    public void canGetDateTimeConverter() throws NoSuchMethodException {
        Converter converter = Converter.forMethod(EntityWithDate.class.getMethod("getDateTime"));

        assertThat(converter, notNullValue());
        assertThat(converter, instanceOf(DateTimeConverter.class));

    }

    @Test
    public void canSerializeDate() {
        DateTime date = new DateTime();

        Object value = converter.toDbValue(date);

        assertThat((Long) value, is(date.getMillis()));
    }

    @Test
    public void canDeserializeDate() {
        DateTime date = new DateTime();

        Object value = converter.fromDbValue(date.getMillis());

        assertThat(value, notNullValue());
        assertThat((DateTime) value, is(date));
    }

    private class EntityWithDate {

        private DateTime dateTime;

        public DateTime getDateTime() {
            return dateTime;
        }

        public void setDateTime(DateTime dateTime) {
            this.dateTime = dateTime;
        }
    }
}
