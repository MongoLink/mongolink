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

package org.mongolink.utils;

import org.junit.Test;

import static org.hamcrest.core.Is.*;
import static org.junit.Assert.*;

public class TestsPropertyContainer {

    private class FakeEntity {
        private boolean isOk() {
            return false;
        }

        private String getStuff() {
            return null;
        }
    }

    @Test
    public void canGetShortnameOnStringPropertyMethod() throws Exception {
        PropertyContainer propertyContainer = new PropertyContainer(FakeEntity.class.getDeclaredMethod("getStuff"));

        assertThat(propertyContainer.shortName(), is("stuff"));
    }

    @Test
    public void canGetShortnameOnBooleanPropertyMethod() throws Exception {
        PropertyContainer propertyContainer = new PropertyContainer(FakeEntity.class.getDeclaredMethod("isOk"));

        assertThat(propertyContainer.shortName(), is("ok"));
    }
}
