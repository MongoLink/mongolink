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

import com.mongodb.FakeDB;
import fr.bodysplash.mongolink.MongoSession;
import fr.bodysplash.mongolink.domain.criteria.Criteria;
import fr.bodysplash.mongolink.domain.criteria.Restriction;
import fr.bodysplash.mongolink.domain.criteria.Restrictions;
import fr.bodysplash.mongolink.test.criteria.FakeRestrictionEquals;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class TestsFakePersistentContext {

    @Before
    public void setUp() throws Throwable {
        context = new FakePersistentContext("fr.bodysplash.mongolink.test");
        context.before();
    }

    @Test
    public void canGetSession() {
        final MongoSession session = context.getSession();

        assertThat(session, notNullValue());
    }

    @Test
    public void doesUseAFakeDb() {
        final MongoSession session = context.getSession();

        assertThat(session.getDb(), instanceOf(FakeDB.class));
    }

    @Test
    public void doesUseFakeCriteria() {
        final MongoSession session = context.getSession();

        final Criteria criteria = session.createCriteria(FakeEntity.class);

        assertThat(criteria, instanceOf(FakeCriteria.class));
    }

    @Test
    public void restrictionsAreFake() {
        final Restriction restriction = Restrictions.equals("f", 3);

        assertThat(restriction, instanceOf(FakeRestrictionEquals.class));
    }

    private FakePersistentContext context;
}
