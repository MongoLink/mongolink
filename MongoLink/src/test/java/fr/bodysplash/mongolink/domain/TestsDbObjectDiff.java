package fr.bodysplash.mongolink.domain;


import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class TestsDbObjectDiff {

    @Test
    public void canDiffProperty() {
        final BasicDBObject origin = new BasicDBObject();
        final BasicDBObject dirty = new BasicDBObject();
        origin.append("value", "original");
        dirty.append("value", "new one");

        final DBObject diff = new DbObjectDiff(origin).compareWith(dirty);

        assertThat(diff.containsField("$set"), is(true));
        final DBObject set = (DBObject) diff.get("$set");
        assertThat(set, notNullValue());
        assertThat(set.containsField("value"), is(true));
        assertThat((String) set.get("value"), is("new one"));
    }

    @Test
    public void dontGenerateDiffWhenNoChanges() {
        final BasicDBObject dirty = new BasicDBObject();
        final BasicDBObject origin = new BasicDBObject();
        origin.append("value", "original");
        dirty.append("value", "original");

        final DBObject diff = new DbObjectDiff(origin).compareWith(dirty);

        assertThat(diff.keySet().size(), is(0));
    }

    @Test
    public void canGenerateMulipleDiff() {
        final BasicDBObject dirty = new BasicDBObject();
        final BasicDBObject origin = new BasicDBObject();
        origin.append("value", "original");
        origin.append("other value", " other original");
        dirty.append("value", "new value");
        dirty.append("other value", "new other value");

        final DBObject diff = new DbObjectDiff(origin).compareWith(dirty);

        assertThat(diff.keySet().size(), is(2));
    }
}
