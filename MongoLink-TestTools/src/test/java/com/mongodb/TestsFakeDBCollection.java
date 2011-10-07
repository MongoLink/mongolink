package com.mongodb;

import com.google.common.collect.Lists;
import org.junit.*;

import java.util.List;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;

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
