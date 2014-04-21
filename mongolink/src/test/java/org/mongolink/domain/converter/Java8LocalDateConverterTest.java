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

import java.time.LocalDate;
import java.util.Date;

import static org.fest.assertions.Assertions.assertThat;

public class Java8LocalDateConverterTest {

    @Test
    public void canSerialize() {
        final Object result = new Java8LocalDateConverter().toDbValue(LocalDate.of(2014, 1, 1));


        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(Date.class);
    }

    @Test
    public void canDeserialize() {
        final Object result = new Java8LocalDateConverter().fromDbValue(new Date(0));

        assertThat(result).isEqualTo(LocalDate.of(1970, 1, 1));
    }

    @Test
    public void operationIsSymetric() {
        final LocalDate originalDate = LocalDate.of(2014, 1, 1);
        final Java8LocalDateConverter converter = new Java8LocalDateConverter();
        final Object serialized = converter.toDbValue(originalDate);
        final Object unserialized = converter.fromDbValue(serialized);

        assertThat(unserialized).isEqualTo(originalDate);
    }
}
