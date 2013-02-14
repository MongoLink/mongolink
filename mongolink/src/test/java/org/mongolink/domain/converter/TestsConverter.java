/*
 * MongoLink, Object Document Mapper for Java and MongoDB
 *
 * Copyright (c) 2012, Arpinum or third-party contributors as
 * indicated by the @author tags
 *
 * MongoLink is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MongoLink is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the Lesser GNU General Public License
 * along with MongoLink.  If not, see <http://www.gnu.org/licenses/>. 
 *
 */

package org.mongolink.domain.converter;

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
