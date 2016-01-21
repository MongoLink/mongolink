package org.mongolink.domain.converter;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.junit.Assert;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

public class TestsIntervalConverter {


    @Test
    public void canGetIntervalConverter() throws NoSuchMethodException {
        Converter converter = Converter.forMethod(EntityWithInterval.class.getMethod("getInterval"));

        assertThat(converter).isNotNull();
        assertThat(converter).isInstanceOf(IntervalConverter.class);
    }

    @Test
    public void canSerialize() {
        Interval interval = new Interval(DateTime.now(), DateTime.now().plusDays(1));

        Object value = new IntervalConverter().toDbValue(interval);

        Assert.assertThat((String) value, is(interval.toString()));
    }

    @Test
    public void canDeserialize() {
        Interval interval = new Interval(DateTime.now(), DateTime.now().plusDays(1));

        Object value = new IntervalConverter().fromDbValue(interval.toString());

        Assert.assertThat(value, notNullValue());
        Assert.assertThat((Interval) value, is(interval));
    }

    private class EntityWithInterval {

        private Interval interval;

        public Interval getInterval() {
            return interval;
        }

        public void setInterval(Interval interval) {
            this.interval = interval;
        }
    }
}
