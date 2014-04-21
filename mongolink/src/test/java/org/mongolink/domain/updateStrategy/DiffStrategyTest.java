package org.mongolink.domain.updateStrategy;

import com.mongodb.*;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

public class DiffStrategyTest {

    @Test
    public void pullRequestsAreMadeInAnotherQuery() {
        final DiffStrategy strategy = new DiffStrategy();
        final DBCollection collection = mock(DBCollection.class);
        final BasicDBObject origin = new BasicDBObject();
        final BasicDBObject target = new BasicDBObject();
        final BasicDBList val = new BasicDBList();
        val.add("first");
        origin.put("liste", val);
        target.put("liste", new BasicDBList());

        strategy.update(origin, target, collection);

        Mockito.verify(collection, times(2)).update(any(DBObject.class), any(DBObject.class));
    }

    @Test
    public void dontMakeUpdateIfNoChanges() {
        final DiffStrategy diffStrategy = new DiffStrategy();
        final DBCollection collection = mock(DBCollection.class);

        diffStrategy.update(new BasicDBObject("test", "test"), new BasicDBObject("test", "test"), collection);

        verifyZeroInteractions(collection);
    }
}
