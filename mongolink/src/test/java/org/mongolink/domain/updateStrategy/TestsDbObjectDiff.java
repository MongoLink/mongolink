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

package org.mongolink.domain.updateStrategy;

import com.mongodb.*;
import org.junit.*;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class TestsDbObjectDiff {

    @Before
    public void before() {
        origin = new BasicDBObject();
        dirty = new BasicDBObject();
    }

    @Test
    public void canDiffProperty() {
        addValue("value", "original", "new one");

        final DBObject diff = new DbObjectDiff(origin).compareWith(dirty);

        assertThat(diff.containsField("$set"), is(true));
        final DBObject set = (DBObject) diff.get("$set");
        assertThat(set, notNullValue());
        assertThat(set.containsField("value"), is(true));
        assertThat(set.get("value"), is("new one"));
    }

    @Test
    public void canDiffPropertyWhenOriginalPropertyDoesNotExist() {
        dirty.append("value", "new one");

        final DBObject diff = new DbObjectDiff(origin).compareWith(dirty);

        assertThat(diff.containsField("$set"), is(true));
        final DBObject set = (DBObject) diff.get("$set");
        assertThat(set, notNullValue());
        assertThat(set.containsField("value"), is(true));
        assertThat(set.get("value"), is("new one"));
    }

    @Test
    public void dontGenerateDiffWhenNoChanges() {
        addValue("value", "value", "value");

        final DBObject diff = new DbObjectDiff(origin).compareWith(dirty);

        assertThat(diff.keySet().size(), is(0));
    }

    @Test
    public void canGenerateMulipleDiff() {
        addValue("value", "original", "new value");
        addValue("other value", "other", "new other value");

        final DBObject diff = new DbObjectDiff(origin).compareWith(dirty);

        final DBObject $set = (DBObject) diff.get("$set");
        assertThat($set.keySet().size(), is(2));
        assertThat($set.get("value"), is("new value"));
        assertThat($set.get("other value"), is("new other value"));
    }

    @Test
    public void canGeneratePush() {
        BasicDBList originalList = new BasicDBList();
        BasicDBList dirtyList = new BasicDBList();
        originalList.add("original");
        dirtyList.add("original");
        dirtyList.add("new value");
        addValue("list", originalList, dirtyList);

        final DBObject diff = new DbObjectDiff(origin).compareWith(dirty);

        final DBObject push = (DBObject) diff.get("$push");
        assertThat(push, notNullValue());
        assertThat(push.keySet().size(), is(1));
        assertThat(push.get("list").toString(), is("new value"));
    }

    @Test
    public void canGeneratePushOnLastElement() {
        BasicDBList originalList = new BasicDBList();
        BasicDBList dirtyList = new BasicDBList();
        originalList.add("original");
        originalList.add("second value");
        dirtyList.add("original");
        dirtyList.add("second value");
        dirtyList.add("new value");
        addValue("list", originalList, dirtyList);

        final DBObject diff = new DbObjectDiff(origin).compareWith(dirty);

        final DBObject push = (DBObject) diff.get("$push");
        assertThat(push.get("list"), is("new value"));
    }

    @Test
    public void dontGeneratePushWhenNoDiff() {
        BasicDBList originalList = new BasicDBList();
        BasicDBList dirtyList = new BasicDBList();
        originalList.add("original");
        dirtyList.add("original");
        addValue("list", originalList, dirtyList);

        final DBObject diff = new DbObjectDiff(origin).compareWith(dirty);

        assertThat(diff.containsField("$push"), is(false));
    }

    @Test
    public void canNavigateInComponent() {
        BasicDBObject subElement = new BasicDBObject();
        subElement.put("test", "old value");
        final BasicDBObject otherSubElement = new BasicDBObject();
        otherSubElement.put("test", "new value");
        addValue("sub", subElement, otherSubElement);
        addValue("firstLevel", "old", "new");

        final DBObject diff = new DbObjectDiff(origin).compareWith(dirty);

        final DBObject $set = (DBObject) diff.get("$set");
        assertThat($set.keySet().size(), is(2));
        assertThat($set.keySet(), hasItem("sub.test"));
        assertThat($set.keySet(), hasItem("firstLevel"));
    }

    @Test
    public void canGeneratePushIntoASubElement() {
        BasicDBObject subElement = new BasicDBObject();
        final BasicDBList val = new BasicDBList();
        val.add("old list value");
        subElement.put("test", val.clone());
        final BasicDBObject otherSubElement = new BasicDBObject();
        val.add("new value");
        otherSubElement.put("test", val);
        addValue("sub", subElement, otherSubElement);

        final DBObject diff = new DbObjectDiff(origin).compareWith(dirty);

        final DBObject $put = (DBObject) diff.get("$push");
        assertThat($put.keySet().size(), is(1));
        assertThat($put.keySet(), hasItem("sub.test"));
    }

    @Test
    public void canGeneratePull() {
        BasicDBList originalList = new BasicDBList();
        BasicDBList dirtyList = new BasicDBList();
        originalList.add("original");
        addValue("list", originalList, dirtyList);

        final DBObject diff = new DbObjectDiff(origin).compareWith(dirty);

        final DBObject pull = (DBObject) diff.get("$pull");
        assertThat(pull, notNullValue());
        assertThat(pull.keySet().size(), is(1));
    }

    @Test
    public void canAddUnset() {
        final DbObjectDiff dbObjectDiff = new DbObjectDiff(new BasicDBObject());
        dbObjectDiff.pushKey("key");
        dbObjectDiff.addUnset();

        final DBObject result = dbObjectDiff.compareWith(new BasicDBObject());

        assertThat(result.get("$unset"), notNullValue());
        final DBObject unset = (DBObject) result.get("$unset");
        assertThat(unset.get("key"), is(1));
    }

    @Test
    public void canAdPullWithNull() {
        final DbObjectDiff dbObjectDiff = new DbObjectDiff(new BasicDBObject());
        dbObjectDiff.pushKey("key");
        dbObjectDiff.addPull(null);

        final DBObject result = dbObjectDiff.compareWith(new BasicDBObject());

        final DBObject pull = (DBObject) result.get("$pull");
        assertThat(pull, notNullValue());
        assertThat(pull.get("key"), nullValue());
    }

    private void addValue(String key, Object originalValue, Object dirtyValue) {
        origin.append(key, originalValue);
        dirty.append(key, dirtyValue);
    }

    private BasicDBObject origin;
    private BasicDBObject dirty;
}
