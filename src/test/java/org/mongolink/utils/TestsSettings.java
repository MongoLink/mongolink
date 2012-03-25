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

import org.junit.Test;
import org.mongolink.*;
import org.mongolink.domain.*;
import org.mongolink.domain.criteria.*;
import org.mongolink.test.factory.FakeDbFactory;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class TestsSettings {

    @Test
    public void canCreateDbFactory() {
        Settings settings = Settings.defaultInstance().withPort(1234).withHost("localhost").withDbFactory(FakeDbFactory.class);

        FakeDbFactory dbFactory = (FakeDbFactory) settings.createDbFactory();

        assertThat(dbFactory.host, is("localhost"));
        assertThat(dbFactory.port, is(1234));
    }

    @Test
    public void canCreateDefaultSettings() {
        Settings settings = Settings.defaultInstance();

        assertThat(settings, notNullValue());
        DbFactory dbFactory = settings.createDbFactory();
        assertThat(dbFactory, notNullValue());
        assertThat(dbFactory, not(instanceOf(FakeDbFactory.class)));
        assertThat(dbFactory.getPort(), is(27017));
        assertThat(dbFactory.getHost(), is("127.0.0.1"));
        assertThat(settings.getDbName(), is("test"));
        assertThat(settings.getUpdateStrategy(), is(UpdateStrategies.OVERWRITE));
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
