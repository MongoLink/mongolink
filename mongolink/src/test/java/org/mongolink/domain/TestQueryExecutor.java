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

import com.github.fakemongo.Fongo;
import com.mongodb.*;
import com.mongodb.client.*;
import org.bson.Document;
import org.junit.Test;
import org.mockito.Matchers;
import org.mongolink.domain.mapper.AggregateMapper;
import org.mongolink.domain.query.CursorParameter;
import org.mongolink.domain.query.QueryExecutor;
import org.mongolink.domain.session.MongoSessionImpl;
import org.mongolink.domain.session.UnitOfWork;
import org.mongolink.test.entity.FakeAggregate;

import java.util.List;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
public class TestQueryExecutor {

    @Test
    public void canLimit() {
        final QueryExecutor<FakeAggregate> executor = createQueryExecutor();

        final List<FakeAggregate> list = executor.execute(new BasicDBObject(), CursorParameter.withLimit(10));

        assertThat(list.size(), is(10));
    }

    @Test
    public void canSkip() {
        final QueryExecutor<FakeAggregate> executor = createQueryExecutor();

        final List<FakeAggregate> list = executor.execute(new BasicDBObject(), CursorParameter.withSkip(11));

        assertThat(list.size(), is(9));
    }

    @Test
    public void canSort() {
        final QueryExecutor<FakeAggregate> executor = createQueryExecutor();

        final List<FakeAggregate> list = executor.execute(new BasicDBObject(), CursorParameter.withSort("value", 11));

        assertThat(list.size(), is(20));
    }

    private QueryExecutor createQueryExecutor() {
        final MongoDatabase db = new Fongo("test").getDatabase("test");
        MongoCollection<Document> collection = db.getCollection("collection");
        for (int i = 0; i < 20; i++) {
            final Document element = new Document();
            element.put("value", i);
            collection.insertOne(element);
        }
        final AggregateMapper aggregateMapper = mock(AggregateMapper.class);
        when(aggregateMapper.collectionName()).thenReturn("collection");
        when(aggregateMapper.toInstance(Matchers.any())).thenReturn(new FakeAggregate("gfg"));
        return new QueryExecutor<>(db, aggregateMapper, new UnitOfWork(mock(MongoSessionImpl.class)));
    }

}
