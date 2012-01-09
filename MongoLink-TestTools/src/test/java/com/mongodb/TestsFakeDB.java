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
