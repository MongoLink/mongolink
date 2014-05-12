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

package org.mongolink.domain.criteria;

import com.google.common.collect.Lists;
import com.mongodb.*;
import org.joda.time.DateTime;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mongolink.domain.query.CursorParameter;
import org.mongolink.domain.query.QueryExecutor;

import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class TestsCriteria {

    @Test
    public void canTestEquality() {
        final Criteria criteria = new Criteria(mock(QueryExecutor.class));
        criteria.add(Restrictions.equals("id", "test"));

        DBObject query = criteria.createQuery();

        assertThat(query, notNullValue());
        assertThat(query.get("id"), is((Object) "test"));
    }

    @Test
    public void canTestMultipleEquality() {
        final Criteria criteria = new Criteria(mock(QueryExecutor.class));
        criteria.add(Restrictions.equals("id", "test"));
        criteria.add(Restrictions.equals("toto", "tata"));

        DBObject query = criteria.createQuery();

        assertThat(query, notNullValue());
        assertThat(query.get("id"), is((Object) "test"));
        assertThat(query.get("toto"), is((Object) "tata"));
    }

    @Test
    public void canTestBetween() {
        final Criteria criteria = new Criteria(mock(QueryExecutor.class));
        criteria.add(Restrictions.between("date", 1, 2));

        DBObject query = criteria.createQuery();

        DBObject restriction = (DBObject) query.get("date");
        assertThat(restriction, notNullValue());
        assertThat(restriction.get("$gte"), is((Object) 1));
        assertThat(restriction.get("$lt"), is((Object) 2));
    }

    @Test
    public void canUseConverterInEq() {
        final Criteria criteria = new Criteria(mock(QueryExecutor.class));
        final DateTime date = new DateTime();
        criteria.add(Restrictions.equals("date", date));

        DBObject query = criteria.createQuery();

        assertThat(query, notNullValue());
        assertThat(query.get("date"), is((Object) date.getMillis()));
    }

    @Test
    public void canUseConverterInBetween() {
        final Criteria criteria = new Criteria(mock(QueryExecutor.class));
        final DateTime date = new DateTime();
        criteria.add(Restrictions.between("date", date, date));

        DBObject query = criteria.createQuery();

        DBObject restriction = (DBObject) query.get("date");
        assertThat(restriction, notNullValue());
        assertThat(restriction.get("$gte"), is((Object) date.getMillis()));
        assertThat(restriction.get("$lt"), is((Object) date.getMillis()));
    }

    @Test
    public void canGiveLimitAndSkip() {
        final QueryExecutor executor = mock(QueryExecutor.class);
        final Criteria criteria = new Criteria(executor);
        criteria.limit(10);
        criteria.skip(3);

        criteria.list();

        final ArgumentCaptor<CursorParameter> captor = ArgumentCaptor.forClass(CursorParameter.class);
        verify(executor).execute(any(DBObject.class), captor.capture());
        final CursorParameter cursorParameter = captor.getValue();
        assertThat(cursorParameter.getLimit(), is(10));
        assertThat(cursorParameter.getSkip(), is(3));
    }

    @Test
    public void canSort() {
        final QueryExecutor executor = mock(QueryExecutor.class);
        final Criteria criteria = new Criteria(executor);
        criteria.sort("field", Order.ASCENDING);

        criteria.list();

        final ArgumentCaptor<CursorParameter> captor = ArgumentCaptor.forClass(CursorParameter.class);
        verify(executor).execute(any(DBObject.class), captor.capture());
        final CursorParameter cursorParameter = captor.getValue();
        final BasicDBObject sortQuery = cursorParameter.getSort();
        assertTrue(sortQuery.containsKey((Object) "field"));
        assertTrue(sortQuery.get("field").equals(1));
    }

    @Test
    public void TestInequality() {
        final Criteria criteria = new Criteria(mock(QueryExecutor.class));
        criteria.add(Restrictions.notEquals("text", "value"));

        DBObject query = criteria.createQuery();

        DBObject restriction = (DBObject) query.get("text");
        assertThat(restriction, notNullValue());
        assertThat(restriction.get("$ne"), is((Object) "value"));
    }

    @Test
    public void canTestGreaterThanOrEqualTo() {
        final Criteria criteria = new Criteria(mock(QueryExecutor.class));
        criteria.add(Restrictions.greaterThanOrEqualTo("date", 1));

        DBObject query = criteria.createQuery();

        DBObject restriction = (DBObject) query.get("date");
        assertThat(restriction, notNullValue());
        assertThat(restriction.get("$gte"), is((Object) 1));
    }

    @Test
    public void canTestEqualityWithRegex() {
        final Criteria criteria = new Criteria(mock(QueryExecutor.class));
        criteria.add(Restrictions.equalsToRegex("date", "regex"));

        DBObject query = criteria.createQuery();

        DBObject restriction = (DBObject) query.get("date");
        assertThat(restriction, notNullValue());
        assertThat(restriction.get("$regex"), is((Object) "regex"));
    }

    @Test
    public void canUseInOperator() {
        final Criteria criteria = new Criteria(mock(QueryExecutor.class));
        List<Object> tokens = Lists.newArrayList();
        tokens.add("blue");
        tokens.add("");
        criteria.add(Restrictions.in("colors", tokens));

        DBObject query = criteria.createQuery();

        DBObject restriction = (DBObject) query.get("colors");
        assertThat(restriction, notNullValue());
        final BasicDBList objects = new BasicDBList();
        objects.add("blue");
        objects.add("");
        assertThat(restriction.get("$in"), is((Object) objects));
    }

    @Test
    public void canUseInOperatorWithUUID() {
        final Criteria criteria = new Criteria(mock(QueryExecutor.class));
        List<Object> tokens = Lists.newArrayList();
        final UUID uuid = UUID.randomUUID();
        tokens.add(uuid);
        criteria.add(Restrictions.in("colors", tokens));

        DBObject query = criteria.createQuery();

        DBObject restriction = (DBObject) query.get("colors");
        assertThat(restriction, notNullValue());
        final BasicDBList objects = new BasicDBList();
        objects.add(uuid);
        assertThat(restriction.get("$in"), is((Object) objects));
    }
}
