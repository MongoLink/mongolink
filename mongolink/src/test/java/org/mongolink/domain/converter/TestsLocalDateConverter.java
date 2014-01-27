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


import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class TestsLocalDateConverter {

    @Before
    public void setUp() throws Exception {
        converter = new LocalDateConverter();
    }

    @Test
    public void canGetLocalDateConverter() throws NoSuchMethodException {
        Converter converter = Converter.forMethod(EntityWithLocalDate.class.getMethod("getLocalDate"));

        assertThat(converter, notNullValue());
        assertThat(converter, instanceOf(LocalDateConverter.class));

    }

    @Test
    public void canSerializeLocalDate() {
        LocalDate localDate = new LocalDate();

        Object value = converter.toDbValue(localDate);

        assertThat((Long) value, is(localDate.toDateTimeAtStartOfDay().getMillis()));
    }

    @Test
    public void canDeserializeLocalDate() {
        LocalDate localDate = new LocalDate();

        Object value = converter.fromDbValue(localDate.toDateTimeAtStartOfDay().getMillis());

        assertThat(value, notNullValue());
        assertThat((LocalDate) value, is(localDate));
    }

    private LocalDateConverter converter;

    private class EntityWithLocalDate {

        public LocalDate getLocalDate() {
            return localDate;
        }

        public void setLocalDate(LocalDate localDate) {
            this.localDate = localDate;
        }

        private LocalDate localDate;
    }
}
