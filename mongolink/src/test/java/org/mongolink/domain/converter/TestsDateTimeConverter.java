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

    @Test
    public void canDealWithNull() {
        final Object value = converter.fromDbValue(null);

        assertThat(value, nullValue());
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
