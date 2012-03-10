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

package org.mongolink.domain;

import com.mongodb.*;
import org.junit.Test;
import org.mockito.Matchers;
import org.mongolink.MongoSession;
import org.mongolink.domain.mapper.EntityMapper;
import org.mongolink.test.entity.FakeEntity;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
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

    @Test
    public void canSort() {
        final QueryExecutor<FakeEntity> executor = createQueryExecutor();

        final List<FakeEntity> list = executor.execute(new BasicDBObject(), CursorParameter.withSort("value", 11));

        assertThat(list.size(), is(20));
    }

    private QueryExecutor createQueryExecutor() {
        final DB db = new FakeDB();
        FakeDBCollection collection = (FakeDBCollection) db.getCollection("collection");
        for (int i = 0; i < 20; i++) {
            final BasicDBObject element = new BasicDBObject();
            element.put("value", i);
            collection.getObjects().add(element);
        }
        final EntityMapper entityMapper = mock(EntityMapper.class);
        when(entityMapper.collectionName()).thenReturn("collection");
        when(entityMapper.toInstance(Matchers.<DBObject>any())).thenReturn(new FakeEntity("gfg"));
        return new QueryExecutor<FakeEntity>(db, entityMapper, new UnitOfWork(mock(MongoSession.class)));
    }

}
