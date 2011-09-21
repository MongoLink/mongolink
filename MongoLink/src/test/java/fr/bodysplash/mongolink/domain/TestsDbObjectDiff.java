package fr.bodysplash.mongolink.domain;


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
        assertThat((String) set.get("value"), is("new one"));
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
        assertThat((String) $set.get("value"), is("new value"));
        assertThat((String) $set.get("other value"), is("new other value"));
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
        assertThat((String) push.get("list"), is("new value"));
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

    private void addValue(String key, Object originalValue, Object dirtyValue) {
        origin.append(key, originalValue);
        dirty.append(key, dirtyValue);
    }

    private BasicDBObject origin;
    private BasicDBObject dirty;
}
