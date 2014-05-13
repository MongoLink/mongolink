
/*
 * MongoLink, Object Document Mapper for Java and MongoDB
 *
 * Copyright (c) 2012, Arpinum or third-party contributors as
 * indicated by the contributors.txt file
 *
 * MongoLink is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * MongoLink is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the Lesser GNU General Public License
 * along with MongoLink.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.mongolink.domain.session;


import com.github.fakemongo.Fongo;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import org.junit.Before;
import org.junit.Test;
import org.mongolink.domain.criteria.CriteriaFactory;
import org.mongolink.domain.mapper.MapperContext;
import org.mongolink.test.entity.FakeAggregate;
import org.mongolink.test.entity.FakeChildAggregate;
import org.mongolink.test.inheritanceMapping.FakeAggregateWithSubclassMapping;

import static org.fest.assertions.Assertions.*;

public class TestsMongoSessionWithInheritance {

    @Before
    public void before() {
        db = new Fongo("test").getDB("test");
        FakeAggregateWithSubclassMapping mapping = new FakeAggregateWithSubclassMapping();
        session = new MongoSessionImpl(db, new CriteriaFactory());
        MapperContext context = new MapperContext();
        mapping.buildMapper(context);
        session.setMappingContext(context);
        session.start();
    }

    @Test
    public void canGetChildEntitiesFromParent() {
        BasicDBObject dbo = new BasicDBObject();
        dbo.put("_id", "1");
        dbo.put("__discriminator", "FakeChildAggregate");
        entities().insert(dbo);

        FakeAggregate entity = session.get("1", FakeAggregate.class);

        assertThat(entity).isNotNull();
        assertThat(entity).isInstanceOf(FakeChildAggregate.class);
    }

    @Test
    public void canGetByChildType() {
        BasicDBObject dbo = new BasicDBObject();
        dbo.put("_id", "1");
        dbo.put("__discriminator", "FakeChildAggregate");
        entities().insert(dbo);

        FakeAggregate entity = session.get("1", FakeChildAggregate.class);

        assertThat(entity).isNotNull();

    }

    @Test
    public void savesChildEntityInSameCollection() {
        FakeChildAggregate fakeChildEntity = new FakeChildAggregate();
        fakeChildEntity.setId("2");

        session.save(fakeChildEntity);
        session.flush();

        assertThat(entities().count()).isEqualTo(1L);
    }

    private com.mongodb.DBCollection entities() {
        return db.getCollection("fakeaggregate");
    }

    private MongoSessionImpl session;
    private DB db;
}
