package fr.bodysplash.mongolink.domain;

import com.mongodb.*;
import fr.bodysplash.mongolink.MongoSession;
import fr.bodysplash.mongolink.domain.mapper.EntityMapper;
import fr.bodysplash.mongolink.test.entity.FakeEntity;
import org.junit.Test;
import org.mockito.Matchers;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.*;

public class TestQueryExecutor {

    @Test
    public void canLimit() {
        final QueryExecutor<FakeEntity> executor = createQueryExecutor();

        final List<FakeEntity> list = executor.execute(new BasicDBObject(), CursorParameter.withLimit(10));

        assertThat(list.size(), is(10));
    }

    @Test
    public void canSkip() {
        final QueryExecutor<FakeEntity> executor = createQueryExecutor();

        final List<FakeEntity> list = executor.execute(new BasicDBObject(), CursorParameter.withSkip(11));

        assertThat(list.size(), is(9));
    }

    private QueryExecutor createQueryExecutor() {
        final DB db = new FakeDB();
        collection = (FakeDBCollection) db.getCollection("collection");
        for(int i = 0; i<20; i++) {
            collection.getObjects().add(new BasicDBObject());
        }
        final EntityMapper entityMapper = mock(EntityMapper.class);
        when(entityMapper.collectionName()).thenReturn("collection");
        when(entityMapper.toInstance(Matchers.<DBObject>any())).thenReturn(new FakeEntity("gfg"));
        return new QueryExecutor<FakeEntity>(db, entityMapper, new UnitOfWork(mock(MongoSession.class)));
    }

    private FakeDBCollection collection;
}
