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

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class TestsFakeDBCollection {

    private FakeDBCollection collection;

    @Before
    public void setUp() throws Exception {
        collection = new FakeDBCollection(new FakeDB(), "test");
    }

    @Test
    public void canFindOne() {
        addObject(collection);
        DBObject query = new BasicDBObject();
        query.put("_id", "3");

        final DBObject feedFound = collection.findOne(query);

        assertThat(feedFound, notNullValue());
        assertThat(feedFound.get("_id"), is((Object) "3"));
    }

    private void addObject(FakeDBCollection collection) {
        DBObject dbo = new BasicDBObject();
        dbo.put("_id", "3");
        collection.insert(dbo);
    }

    @Test
    public void canSetAutoId() {
        List<DBObject> objects = Lists.newArrayList();
        BasicDBObject dbo = new BasicDBObject();
        objects.add(dbo);
        BasicDBObject other = new BasicDBObject();
        objects.add(other);

        collection.insert(objects);

        assertThat(dbo.get("_id"), notNullValue());
        assertThat(other.get("_id"), not(dbo.get("_id")));
    }
}
