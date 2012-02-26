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

package com.mongodb;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class TestsFakeDB {

    @Test
    public void canGetACollectionFromFakeDB() {
        final FakeDB fakeDB = new FakeDB();

        final DBCollection test = fakeDB.doGetCollection("test");

        assertThat(test, notNullValue());
    }

    @Test
    public void canUseRealGetCollectionMethod() {
        final FakeDB fakeDB = new FakeDB();

        final DBCollection test = fakeDB.getCollection("test");

        assertThat(test, notNullValue());
    }

    @Test
    public void canCreateAFakeCollection() {
        final FakeDB fakeDB = new FakeDB();

        fakeDB.createCollection("test", new BasicDBObject());

        assertThat(fakeDB.collections.size(), is(1));
    }
}
