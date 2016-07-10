package org.mongolink.domain.updateStrategy;

import com.google.common.collect.Lists;
import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.mockito.Mockito.*;

public class DiffStrategyTest {

    @Test
    public void pullRequestsAreMadeInAnotherQuery() {
        final DiffStrategy strategy = new DiffStrategy();
        final MongoCollection collection = mock(MongoCollection.class);
        final Document origin = new Document();
        final Document target = new Document();
        final List<Object> val = Lists.newArrayList();
        val.add("first");
        val.add("second");
        origin.put("liste", val);
        final List<Object> targetList = Lists.newArrayList();
        targetList.add("second");
        targetList.add("third");
        targetList.add("other");
        target.put("liste", targetList);

        strategy.update(origin, target, collection);

        Mockito.verify(collection, times(2)).updateOne(any(Document.class), any(Document.class));
    }

    @Test
    public void pushRequestsAreMadeInAnotherQuery() {
        final DiffStrategy strategy = new DiffStrategy();
        final MongoCollection collection = mock(MongoCollection.class);
        when(collection.getNamespace()).thenReturn(new MongoNamespace("test", "coll"));

        final Document origin = new Document();
        final Document target = new Document();
        final List<Object> val = Lists.newArrayList();
        val.add("first");
        val.add("second");
        origin.put("liste", val);
        final List<Object> targetList = Lists.newArrayList();
        targetList.add("second");
        targetList.add("first");
        targetList.add("third");
        target.put("liste", targetList);

        strategy.update(origin, target, collection);

        Mockito.verify(collection, times(2)).updateOne(any(Bson.class), any(Document.class));
    }

    @Test
    public void dontMakeUpdateIfNoChanges() {
        final DiffStrategy diffStrategy = new DiffStrategy();
        final MongoCollection collection = mock(MongoCollection.class);

        diffStrategy.update(new Document("test", "test"), new Document("test", "test"), collection);

        verifyZeroInteractions(collection);
    }
}
