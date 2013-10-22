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

    @Test
    public void canGetShortnameOnStringPropertyMethod() throws Exception {
        FieldContainer fieldContainer = new FieldContainer(FakeEntity.class.getDeclaredMethod("getStuff"));

        assertThat(fieldContainer.name(), is("stuff"));
    }

    @Test
    public void canGetShortnameOnBooleanPropertyMethod() throws Exception {
        FieldContainer fieldContainer = new FieldContainer(FakeEntity.class.getDeclaredMethod("isOk"));

        assertThat(fieldContainer.name(), is("ok"));
    }

    @Test
    public void canSetValueForPrivateField() throws NoSuchMethodException {
        FieldContainer fieldContainer = new FieldContainer(FakeEntity.class.getDeclaredMethod("isOk"));
        FakeEntity entity = new FakeEntity();

        fieldContainer.setValueIn(true, entity);

        assertThat(entity.isOk(), is(true));
    }

    @Test
    public void canGetValueFromPrivateField() throws NoSuchMethodException {
        FieldContainer fieldContainer = new FieldContainer(FakeEntity.class.getDeclaredMethod("getStuff"));
        FakeEntity entity = new FakeEntity();
        entity.stuff = "stuuuf";

        Object stuff = fieldContainer.value(entity);

        assertThat((String) stuff, is("stuuuf"));
    }

    private class FakeEntity {
        private boolean isOk() {
            return ok;
        }

        private String getStuff() {
            return stuff;
        }

        private boolean ok;
        private String stuff;
    }
}
