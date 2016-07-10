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

import com.google.common.collect.Lists;
import org.bson.Document;
import org.junit.*;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class TestsDbObjectDiff {

    @Before
    public void before() {
        origin = new Document();
        dirty = new Document();
    }

    @Test
    public void canDiffProperty() {
        addValue("value", "original", "new one");

        final Document diff = new DbObjectDiff(origin).compareWith(dirty);

        assertThat(diff.containsKey("$set"), is(true));
        final Document set = (Document) diff.get("$set");
        assertThat(set, notNullValue());
        assertThat(set.containsKey("value"), is(true));
        assertThat(set.get("value"), is("new one"));
    }

    @Test
    public void canDiffPropertyWhenOriginalPropertyDoesNotExist() {
        dirty.append("value", "new one");

        final Document diff = new DbObjectDiff(origin).compareWith(dirty);

        assertThat(diff.containsKey("$set"), is(true));
        final Document set = (Document) diff.get("$set");
        assertThat(set, notNullValue());
        assertThat(set.containsKey("value"), is(true));
        assertThat(set.get("value"), is("new one"));
    }

    @Test
    public void dontGenerateDiffWhenNoChanges() {
        addValue("value", "value", "value");

        final Document diff = new DbObjectDiff(origin).compareWith(dirty);

        assertThat(diff.keySet().size(), is(0));
    }

    @Test
    public void canGenerateMulipleDiff() {
        addValue("value", "original", "new value");
        addValue("other value", "other", "new other value");

        final Document diff = new DbObjectDiff(origin).compareWith(dirty);

        final Document $set = (Document) diff.get("$set");
        assertThat($set.keySet().size(), is(2));
        assertThat($set.get("value"), is("new value"));
        assertThat($set.get("other value"), is("new other value"));
    }

    @Test
    public void canGeneratePush() {
        List<Object> originalList = Lists.newArrayList();
        List<Object> dirtyList = Lists.newArrayList();
        originalList.add("original");
        dirtyList.add("original");
        dirtyList.add("new value");
        addValue("list", originalList, dirtyList);

        final Document diff = new DbObjectDiff(origin).compareWith(dirty);

        final Document push = (Document) diff.get("$push");
        assertThat(push, notNullValue());
        assertThat(push.keySet().size(), is(1));
        assertThat(push.get("list").toString(), is("new value"));
    }

    @Test
    public void canGeneratePushOnLastElement() {
        List<Object> originalList = Lists.newArrayList();
        List<Object> dirtyList = Lists.newArrayList();
        originalList.add("original");
        originalList.add("second value");
        dirtyList.add("original");
        dirtyList.add("second value");
        dirtyList.add("new value");
        addValue("list", originalList, dirtyList);

        final Document diff = new DbObjectDiff(origin).compareWith(dirty);

        final Document push = (Document) diff.get("$push");
        assertThat(push.get("list"), is("new value"));
    }

    @Test
    public void dontGeneratePushWhenNoDiff() {
        List<Object> originalList = Lists.newArrayList();
        List<Object> dirtyList = Lists.newArrayList();
        originalList.add("original");
        dirtyList.add("original");
        addValue("list", originalList, dirtyList);

        final Document diff = new DbObjectDiff(origin).compareWith(dirty);

        assertThat(diff.containsKey("$push"), is(false));
    }

    @Test
    public void canNavigateInComponent() {
        Document subElement = new Document();
        subElement.put("test", "old value");
        final Document otherSubElement = new Document();
        otherSubElement.put("test", "new value");
        addValue("sub", subElement, otherSubElement);
        addValue("firstLevel", "old", "new");

        final Document diff = new DbObjectDiff(origin).compareWith(dirty);

        final Document $set = (Document) diff.get("$set");
        assertThat($set.keySet().size(), is(2));
        assertThat($set.keySet(), hasItem("sub.test"));
        assertThat($set.keySet(), hasItem("firstLevel"));
    }

    @Test
    public void canGeneratePushIntoASubElement() {
        Document subElement = new Document();
        final List<Object> val = Lists.newArrayList();
        val.add("old list value");
        subElement.put("test", Lists.newArrayList(val));
        final Document otherSubElement = new Document();
        val.add("new value");
        otherSubElement.put("test", val);
        addValue("sub", subElement, otherSubElement);

        final Document diff = new DbObjectDiff(origin).compareWith(dirty);

        final Document $put = (Document) diff.get("$push");
        assertThat($put.keySet().size(), is(1));
        assertThat($put.keySet(), hasItem("sub.test"));
    }

    @Test
    public void canGeneratePull() {
        List<Object> originalList = Lists.newArrayList();
        List<Object> dirtyList = Lists.newArrayList();
        originalList.add("original");
        addValue("list", originalList, dirtyList);

        final Document diff = new DbObjectDiff(origin).compareWith(dirty);

        final Document pull = (Document) diff.get("$pull");
        assertThat(pull, notNullValue());
        assertThat(pull.keySet().size(), is(1));
    }

    @Test
    public void canAddUnset() {
        final DbObjectDiff dbObjectDiff = new DbObjectDiff(new Document());
        dbObjectDiff.pushKey("key");
        dbObjectDiff.addUnset();

        final Document result = dbObjectDiff.compareWith(new Document());

        assertThat(result.get("$unset"), notNullValue());
        final Document unset = (Document) result.get("$unset");
        assertThat(unset.get("key"), is(1));
    }

    @Test
    public void canAdPullWithNull() {
        final DbObjectDiff dbObjectDiff = new DbObjectDiff(new Document());
        dbObjectDiff.pushKey("key");
        dbObjectDiff.addPull(null);

        final Document result = dbObjectDiff.compareWith(new Document());

        final Document pull = (Document) result.get("$pull");
        assertThat(pull, notNullValue());
        assertThat(pull.get("key"), nullValue());
    }

    private void addValue(String key, Object originalValue, Object dirtyValue) {
        origin.append(key, originalValue);
        dirty.append(key, dirtyValue);
    }

    private Document origin;
    private Document dirty;
}
