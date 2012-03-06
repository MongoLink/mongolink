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

package org.mongolink;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mongolink.MongoSession;
import org.mongolink.MongoSessionManager;
import org.mongolink.Settings;
import org.mongolink.domain.UpdateStrategies;
import org.mongolink.domain.mapper.ContextBuilder;
import org.mongolink.domain.mapper.EntityMapper;
import org.mongolink.domain.mapper.MapperContext;
import org.mongolink.domain.updateStrategy.DiffStrategy;
import org.mongolink.test.entity.FakeEntity;
import org.mongolink.test.entity.FakeEntityWithCap;
import org.mongolink.test.factory.FakeDbFactory;
import org.mongolink.test.factory.TestFactory;

import java.net.UnknownHostException;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

public class TestsMongoSessionManager {

    @Before
    public void before() {
        contextBuilder = TestFactory.contextBuilder().withFakeEntity();
        final Settings settings = Settings.defaultInstance().withDbFactory(FakeDbFactory.class).withDefaultUpdateStrategy(UpdateStrategies.DIFF);
        manager = MongoSessionManager.create(contextBuilder, settings);
    }

    @After
    public void after() throws UnknownHostException {
        manager.close();
    }

    @Test
    public void canCreateFromContextBuilder() {
        assertThat(manager, notNullValue());
        assertThat(manager.getMapperContext(), notNullValue());
        assertThat(manager.getMapperContext().mapperFor(FakeEntity.class), notNullValue());
    }

    @Test
    public void canCreateSession() {
        MongoSession session = manager.createSession();

        assertThat(session, notNullValue());
    }

    @Test
    public void canGetCriteria() {
        MongoSession session = manager.createSession();

        assertThat(session.createCriteria(FakeEntity.class), notNullValue());
    }

    @Test
    public void canSave() {
        MongoSession session = manager.createSession();

        session.save(new FakeEntity("id"));

        assertThat(session.getDb().getCollection("fakeentity").count(), is(1L));
    }

    @Test
    public void canSetUpdateStrategy() {
        final MongoSession session = manager.createSession();

        assertThat(session.getUpdateStrategy(), instanceOf(DiffStrategy.class));
    }

    @Test
    @Ignore("trop mal codé pour le moment, je ne vais pas essayer de le tester je referai tout proprement")
    public void canSetCappedCollections() {
        final MapperContext mapperContext = manager.getMapperContext();
        final EntityMapper<FakeEntityWithCap> fakeEntityWithCapMapper = (EntityMapper<FakeEntityWithCap>) mapperContext.mapperFor(FakeEntityWithCap.class);
        final MongoSession session = manager.createSession();

        assertThat(session.getDb().getCollection(fakeEntityWithCapMapper.collectionName()).isCapped(), is(true));
        assertThat(fakeEntityWithCapMapper.getCappedSize(), is(1048076));
        assertThat(fakeEntityWithCapMapper.getCappedMax(), is(50));
    }

    private static ContextBuilder contextBuilder;
    private static MongoSessionManager manager;
}
