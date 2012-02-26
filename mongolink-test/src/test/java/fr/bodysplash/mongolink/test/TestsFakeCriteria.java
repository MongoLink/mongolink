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

package fr.bodysplash.mongolink.test;

import fr.bodysplash.mongolink.MongoSession;
import fr.bodysplash.mongolink.domain.criteria.Criteria;
import fr.bodysplash.mongolink.domain.criteria.Restrictions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class TestsFakeCriteria {

    @Rule
    public FakePersistentContext fakePersistentContext = new FakePersistentContext("fr.bodysplash.mongolink.test.mapping");

    @Before
    public void setUp() throws Exception {
        session = fakePersistentContext.getSession();
    }

    @Test
    public void canSearchByEquality() {
        savedEntityWithValue(3);
        savedEntityWithValue(2);
        final Criteria criteria = session.createCriteria(FakeEntity.class);
        criteria.add(Restrictions.equals("value", 3));

        final List<FakeEntity> list = criteria.list();

        assertThat(list.size(), is(1));
        assertThat(list.get(0).getValue(), is(3));
    }

    @Test
    public void canSearchBetween() {
        savedEntityWithValue(4);
        savedEntityWithValue(10);
        final Criteria criteria = session.createCriteria(FakeEntity.class);
        criteria.add(Restrictions.between("value", 4, 5));

        final List<FakeEntity> list = criteria.list();

        assertThat(list.size(), is(1));
        assertThat(list.get(0).getValue(), is(4));
    }

    @Test
    public void canSearchByEqualityOnId() {
        final String uri = "uri";
        final FakeEntityWithStringId entity = new FakeEntityWithStringId(uri);
        session.save(entity);
        final Criteria criteria = session.createCriteria(FakeEntityWithStringId.class);
        criteria.add(Restrictions.equals("_id", uri));

        final List<FakeEntityWithStringId> list = criteria.list();

        assertThat(list.size(), is(1));
        assertThat(list.get(0).getUri(), is(uri));
    }

    @Test
    public void canLimit() {
        savedEntityWithValue(4);
        savedEntityWithValue(10);
        final Criteria criteria = session.createCriteria(FakeEntity.class);
        criteria.limit(1);

        final List<FakeEntity> list = criteria.list();

        assertThat(list.size(), is(1));
        assertThat(list.get(0).getValue(), is(4));
    }

    @Test
    public void canSkip() {
        savedEntityWithValue(4);
        savedEntityWithValue(10);
        final Criteria criteria = session.createCriteria(FakeEntity.class);
        criteria.skip(1);

        final List<FakeEntity> list = criteria.list();

        assertThat(list.size(), is(1));
        assertThat(list.get(0).getValue(), is(10));
    }

    @Test
    public void canLimitAndSkip() {
        savedEntityWithValue(1);
        savedEntityWithValue(2);
        savedEntityWithValue(3);
        savedEntityWithValue(4);
        final Criteria criteria = session.createCriteria(FakeEntity.class);

        criteria.skip(1);
        criteria.limit(2);

        final List<FakeEntity> list = criteria.list();

        assertThat(list.size(), is(2));
        assertThat(list.get(0).getValue(), is(2));
        assertThat(list.get(1).getValue(), is(3));
    }

    @Test
    public void canSearchByInequality() {
        savedEntityWithValue(3);
        savedEntityWithValue(2);
        final Criteria criteria = session.createCriteria(FakeEntity.class);
        criteria.add(Restrictions.notEquals("value", 3));

        final List<FakeEntity> list = criteria.list();

        assertThat(list.size(), is(1));
        assertThat(list.get(0).getValue(), is(2));
    }

    private void savedEntityWithValue(int value) {
        final FakeEntity element = new FakeEntity();
        element.setValue(value);
        session.save(element);
    }

    private MongoSession session;
}
