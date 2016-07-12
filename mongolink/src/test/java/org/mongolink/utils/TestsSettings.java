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

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.junit.Test;
import org.mongolink.*;
import org.mongolink.domain.criteria.*;
import org.mongolink.domain.query.QueryExecutor;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class TestsSettings {

    @Test
    public void canUseGivenMongoDatabase() throws Exception {
        MongoDatabase database = mock(MongoDatabase.class);
        MongoClient client = mock(MongoClient.class);
        when(client.getDatabase("test")).thenReturn(database);
        Settings settings = Settings.defaultInstance().withDatabase(database);

        MongoDatabase databaseGiven = settings.buildDatabase();

        assertThat(databaseGiven, is(database));
    }

    @Test
    public void canCreateDefaultSettings() {
        Settings settings = Settings.defaultInstance();

        assertThat(settings, notNullValue());
        MongoDatabase database = settings.buildDatabase();
        assertThat(database, notNullValue());
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
