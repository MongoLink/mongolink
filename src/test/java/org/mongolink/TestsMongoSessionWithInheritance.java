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


import com.mongodb.BasicDBObject;
import com.mongodb.FakeDB;
import com.mongodb.FakeDBCollection;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mongolink.MongoSession;
import org.mongolink.domain.criteria.CriteriaFactory;
import org.mongolink.domain.mapper.MapperContext;
import org.mongolink.test.entity.FakeChildEntity;
import org.mongolink.test.entity.FakeEntity;
import org.mongolink.test.inheritanceMapping.FakeEntityWithSubclassMapping;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class TestsMongoSessionWithInheritance {

    @Before
    public void before() {
        db = Mockito.spy(new FakeDB());
        entities = new FakeDBCollection(db, "fakeentity");
        db.collections.put("fakeentity", entities);
        FakeEntityWithSubclassMapping mapping = new FakeEntityWithSubclassMapping();
        session = new MongoSession(db, new CriteriaFactory());
        MapperContext context = new MapperContext();
        mapping.buildMapper(context);
        session.setMappingContext(context);
    }

    @Test
    public void canGetChildEntitiesFromParent() {
        BasicDBObject dbo = new BasicDBObject();
        dbo.put("_id", "1");
        dbo.put("__discriminator", "FakeChildEntity");
        entities.insert(dbo);

        FakeEntity entity = session.get("1", FakeEntity.class);

        assertThat(entity, notNullValue());
        assertThat(entity, instanceOf(FakeChildEntity.class));
    }

    @Test
    public void savesChildEntityInSameCollection() {
        FakeChildEntity fakeChildEntity = new FakeChildEntity();
        fakeChildEntity.setId("2");

        session.save(fakeChildEntity);

        assertThat(entities.count(), is(1L));
    }

    private FakeDBCollection entities;
    private FakeDB db;
    private MongoSession session;
}
