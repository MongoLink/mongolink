package org.mongolink.domain.converter;

import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.time.Period;

import static org.fest.assertions.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

public class TestsPeriodConverter {

    @Test
    public void canGetPeriodConverter() throws NoSuchMethodException {
        Converter converter = Converter.forMethod(EntityWithPeriod.class.getMethod("getPeriod"));

        assertThat(converter).isNotNull();
        assertThat(converter).isInstanceOf(PeriodConverter.class);
    }

    @Test
    public void canSerialize() {
        Period period = Period.between(LocalDate.now(), LocalDate.now().plusDays(1));

        Object value = new PeriodConverter().toDbValue(period);

        Assert.assertThat((String) value, is(period.toString()));
    }

    @Test
    public void canDeserialize() {
        Period period = Period.between(LocalDate.now(), LocalDate.now().plusDays(1));

        Object value = new PeriodConverter().fromDbValue(period.toString());

        Assert.assertThat(value, notNullValue());
        Assert.assertThat((Period) value, is(period));
    }

    private class EntityWithPeriod {

        private Period period;

        public Period getPeriod() {
            return period;
        }

        public void setPeriod(Period period) {
            this.period = period;
        }
    }
}
