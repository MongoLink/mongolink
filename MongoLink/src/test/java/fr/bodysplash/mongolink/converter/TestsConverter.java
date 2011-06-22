package fr.bodysplash.mongolink.converter;

import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class TestsConverter {

    public enum EnumWithInheritance {
        value {
            @Override
            void test() {
                
            }
        };

        abstract void test();
    }

    public enum EnumWithoutInheritance {
        value
    }

    @Test
    public void canGetEnumConverterFromEnumValueWithInheritance() {
        final Converter converter = Converter.forType(EnumWithInheritance.value.getClass());

        assertThat(converter, instanceOf(EnumConverter.class));
    }

    @Test
    public void canGetEnumConverterFromEnumValueWithoutInheritance() {
        final Converter converter = Converter.forType(EnumWithoutInheritance.value.getClass());

        assertThat(converter, instanceOf(EnumConverter.class));
    }
}
