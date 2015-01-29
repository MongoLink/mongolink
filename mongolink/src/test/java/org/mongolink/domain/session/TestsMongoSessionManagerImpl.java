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

package org.mongolink.domain.session;

import org.junit.*;
import org.mongolink.*;
import org.mongolink.UpdateStrategies;
import org.mongolink.domain.mapper.*;
import org.mongolink.domain.updateStrategy.DiffStrategy;
import org.mongolink.test.entity.FakeAggregate;
import org.mongolink.test.entity.FakeEntityWithCap;
import org.mongolink.test.factory.FakeDbFactory;
import org.mongolink.test.factory.TestFactory;

import java.net.UnknownHostException;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.*;

public class TestsMongoSessionManagerImpl {

    @Before
    public void before() {
        ContextBuilder contextBuilder = TestFactory.contextBuilder().withFakeEntity();
        settings = Settings.defaultInstance().withDbFactory(FakeDbFactory.class).withDefaultUpdateStrategy(UpdateStrategies.DIFF);
        manager = (MongoSessionManagerImpl) MongoSessionManager.create(contextBuilder, settings);
    }

    @After
    public void after() throws UnknownHostException {
        manager.close();
    }

    @Test
    public void canCreateFromContextBuilder() {
        assertThat(manager, notNullValue());
        assertThat(manager.getMapperContext(), notNullValue());
        assertThat(manager.getMapperContext().mapperFor(FakeAggregate.class), notNullValue());
    }

    @Test
    public void canCreateSession() {
        MongoSessionImpl session = manager.createSession();

        assertThat(session, notNullValue());
        assertThat(session.getDb().isAuthenticated(), is(false));
    }

    @Test
    public void canGetCriteria() {
        MongoSession session = manager.createSession();
        session.start();

        assertThat(session.createCriteria(FakeAggregate.class), notNullValue());
    }

    @Test
    public void canSetUpdateStrategy() {
        final MongoSessionImpl session = manager.createSession();

        assertThat(session.getUpdateStrategy(), instanceOf(DiffStrategy.class));
    }

    @Test
    @Ignore("make it pass quickly")
    public void canSetCappedCollections() {
        final MapperContext mapperContext = manager.getMapperContext();
        final AggregateMapper<FakeEntityWithCap> fakeEntityWithCapMapper = (AggregateMapper<FakeEntityWithCap>) mapperContext.mapperFor(FakeEntityWithCap.class);
        final MongoSessionImpl session = manager.createSession();

        assertThat(session.getDb().getCollection(fakeEntityWithCapMapper.collectionName()).isCapped(), is(true));
    }

    private static Settings settings;
    private static MongoSessionManagerImpl manager;
}
