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

package org.mongolink.utils;

import com.google.common.collect.Lists;
import com.mongodb.*;
import org.junit.Test;
import org.mongolink.*;
import org.mongolink.domain.criteria.*;
import org.mongolink.domain.query.QueryExecutor;
import org.mongolink.test.factory.FakeDbFactory;

import java.net.UnknownHostException;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class TestsSettings {

    @Test
    public void canCreateDbFactory() {
        Settings settings = Settings.defaultInstance().withPort(1234).withHost("localhost").withDbFactory(FakeDbFactory.class).withReadPreference(ReadPreference.nearest());

        FakeDbFactory dbFactory = (FakeDbFactory) settings.createDbFactory();

        assertThat(dbFactory.addresses.size(), is(1));
        ServerAddress serverAddress = dbFactory.addresses.get(0);
        assertThat(serverAddress.getHost(), is("localhost"));
        assertThat(serverAddress.getPort(), is(1234));
        assertThat(settings.authenticationRequired(), is(false));
        assertThat(dbFactory.getReadPreference(), is(ReadPreference.nearest()));
    }

    @Test
    public void canCreateDbFactoryWithMultipleAddresses() throws UnknownHostException {
        Settings settings = Settings.defaultInstance().withAddresses(Lists.newArrayList(new ServerAddress("localhost:1234"), new ServerAddress("localhost:1235"))).withDbFactory(FakeDbFactory.class);

        FakeDbFactory dbFactory = (FakeDbFactory) settings.createDbFactory();

        assertThat(dbFactory.addresses.size(), is(2));
    }

    @Test
    public void canCreateSettingsWithAuthentication() {
        Settings settings = Settings.defaultInstance().withAuthentication("user", "passwd").withDbFactory(FakeDbFactory.class);

        assertThat(settings.getUser(), is("user"));
        assertThat(settings.getPassword(), is("passwd"));
        assertThat(settings.authenticationRequired(), is(true));
    }

    @Test
    public void canCreateSettingsWithEmptyAuthentication() {
        Settings settings = Settings.defaultInstance().withAuthentication("", "passwd").withDbFactory(FakeDbFactory.class);

        assertThat(settings.authenticationRequired(), is(false));
    }

    @Test
    public void canCreateDefaultSettings() {
        Settings settings = Settings.defaultInstance();

        assertThat(settings, notNullValue());
        DbFactory dbFactory = settings.createDbFactory();
        assertThat(dbFactory, notNullValue());
        assertThat(dbFactory, not(instanceOf(FakeDbFactory.class)));
        assertThat(dbFactory.getAddresses().size(), is(1));
        ServerAddress serverAddress = dbFactory.getAddresses().get(0);
        assertThat(serverAddress.getPort(), is(27017));
        assertThat(serverAddress.getHost(), is("127.0.0.1"));
        assertThat(settings.getDbName(), is("test"));
        assertThat(settings.getUpdateStrategy(), is(UpdateStrategies.OVERWRITE));
        assertThat(settings.getReadPreference(), is(ReadPreference.primary()));
    }

    @Test
    public void canDefineDbName() {
        Settings settings = Settings.defaultInstance().withDbName("pouette");

        assertThat(settings.getDbName(), is("pouette"));
    }

    @Test
    public void canDefineCriteriaFactory() {
        final Settings settings = Settings.defaultInstance().withCriteriaFactory(DummyCriteriaFactory.class);

        final CriteriaFactory criteriaFactory = settings.getCriteriaFactory();

        assertThat(criteriaFactory, notNullValue());
        assertThat(criteriaFactory, instanceOf(DummyCriteriaFactory.class));
    }

    @Test
    public void canDefineUpdateStrategy() {
        Settings settings = Settings.defaultInstance().withDefaultUpdateStrategy(UpdateStrategies.DIFF);

        assertThat(settings.getUpdateStrategy(), is(UpdateStrategies.DIFF));
    }

    @Test
    public void canDefineReadPreference() {
        Settings settings = Settings.defaultInstance().withReadPreference(ReadPreference.secondary()).withDbFactory(FakeDbFactory.class);

        FakeDbFactory dbFactory = (FakeDbFactory) settings.createDbFactory();

        assertThat(dbFactory.getReadPreference(), is(ReadPreference.secondary()));
    }

    @Test
    public void canDefineWriteConcern() {
        Settings settings = Settings.defaultInstance().withWriteConcern(WriteConcern.ACKNOWLEDGED).withDbFactory(FakeDbFactory.class);

        FakeDbFactory dbFactory = (FakeDbFactory) settings.createDbFactory();

        assertThat(dbFactory.getWriteConcern(), is(WriteConcern.ACKNOWLEDGED));
    }

    public static class DummyCriteriaFactory extends CriteriaFactory {

        @Override
        public Criteria create(QueryExecutor executor) {
            return new DummyCriteria();
        }
    }

    public static class DummyCriteria extends Criteria {
        public DummyCriteria() {
            super(null);
        }
    }
}
