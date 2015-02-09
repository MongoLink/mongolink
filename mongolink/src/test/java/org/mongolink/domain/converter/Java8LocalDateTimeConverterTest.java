/*
 * MongoLink, Object Document Mapper for Java and MongoDB
 *
 * Copyright (c) 2012, Arpinum or third-party contributors as
 * indicated by the contributors.txt file
 *
 * MongoLink is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * MongoLink is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the Lesser GNU General Public License
 * along with MongoLink.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.mongolink.domain.converter;

import org.junit.Test;

import java.time.*;
import java.util.Date;

import static org.fest.assertions.Assertions.*;

public class Java8LocalDateTimeConverterTest {

    @Test
    public void canSerialize() {
        final Object result = new Java8LocalDateTimeConverter().toDbValue(LocalDateTime.of(2014, 1, 1, 17, 30, 20));


        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(Date.class);
    }

    @Test
    public void canDeserialize() {
        final Object result = new Java8LocalDateTimeConverter().fromDbValue(new Date(0));

        assertThat(result).isEqualTo(LocalDateTime.ofInstant(Instant.EPOCH, ZoneId.systemDefault()));
    }

    @Test
    public void operationIsSymetric() {
        final LocalDateTime originalDate = LocalDateTime.of(2014, 1, 1, 17, 10, 23);
        final Java8LocalDateTimeConverter converter = new Java8LocalDateTimeConverter();
        final Object serialized = converter.toDbValue(originalDate);
        final Object unserialized = converter.fromDbValue(serialized);

        assertThat(unserialized).isEqualTo(originalDate);
    }

    @Test
    public void canDealWithNull() {
        final Object value = new Java8LocalDateConverter().fromDbValue(null);

        assertThat(value).isNull();
    }
}
