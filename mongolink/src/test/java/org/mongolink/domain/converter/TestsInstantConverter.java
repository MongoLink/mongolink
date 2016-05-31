package org.mongolink.domain.converter;

import org.junit.Assert;
import org.junit.Test;

import java.time.Instant;

import static org.fest.assertions.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

public class TestsInstantConverter {


    @Test
    public void canGetInstantConverter() throws NoSuchMethodException {
        Converter converter = Converter.forMethod(EntityWithInstant.class.getMethod("getInstant"));

        assertThat(converter).isNotNull();
        assertThat(converter).isInstanceOf(InstantConverter.class);
    }

    @Test
    public void canSerialize() {
        Instant instant = Instant.now();

        Object value = new InstantConverter().toDbValue(instant);

        Assert.assertThat((String) value, is(instant.toString()));
    }

    @Test
    public void canDeserialize() {
        Instant instant = Instant.now();

        Object value = new InstantConverter().fromDbValue(instant.toString());

        Assert.assertThat(value, notNullValue());
        Assert.assertThat((Instant) value, is(instant));
    }

    private class EntityWithInstant {

        private Instant instant;

        public Instant getInstant() {
            return instant;
        }

        public void setInstant(Instant instant) {
            this.instant = instant;
        }
    }
}
