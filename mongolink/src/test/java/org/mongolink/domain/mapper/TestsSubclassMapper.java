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

package org.mongolink.domain.mapper;


import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.mongolink.domain.query.QueryExecutor;
import org.mongolink.domain.criteria.Criteria;
import org.mongolink.test.entity.FakeAggregate;
import org.mongolink.test.entity.FakeChildAggregate;
import org.mongolink.test.simpleMapping.FakeAggregateMapping;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class TestsSubclassMapper {

    @Before
    public void before() {
        createContext();
    }

    @Test
    public void canSaveSubclass() {
        FakeChildAggregate entity = new FakeChildAggregate();
        entity.setValue("this is a value");
        entity.setId("5d9d9b5e36a9a4265ea9ecbe");
        entity.setChildName("this is a name");

        DBObject dbObject = parrentMapper.toDBObject(entity);

        assertThat(dbObject, notNullValue());
        assertThat((String) dbObject.get("value"), is("this is a value"));
        assertThat(dbObject.get("_id"), is((Object) new ObjectId("5d9d9b5e36a9a4265ea9ecbe")));
        assertThat((String) dbObject.get("__discriminator"), is("FakeChildAggregate"));
        assertThat((String) dbObject.get("childName"), is("this is a name"));
    }

    @Test
    public void canPopulateFromDb() {
        BasicDBObject dbo = new BasicDBObject();
        dbo.put("_id", "good id");
        dbo.put("value", "this is a value");
        dbo.put("childName", "this is a name");
        dbo.put("__discriminator", "FakeChildAggregate");

        FakeChildAggregate entity = (FakeChildAggregate) parrentMapper.toInstance(dbo);

        assertThat(entity, notNullValue());
        assertThat(entity.getId(), is("good id"));
        assertThat(entity.getValue(), is("this is a value"));
        assertThat(entity.getChildName(), is("this is a name"));
    }

    @Test
    public void canPopulateFromParentMapper() {
        BasicDBObject dbo = new BasicDBObject();
        dbo.put("_id", "good id");
        dbo.put("value", "this is a value");
        dbo.put("childName", "this is a name");
        dbo.put("__discriminator", "FakeChildAggregate");

        FakeAggregate entity = context.mapperFor(FakeAggregate.class).toInstance(dbo);

        assertThat(entity, instanceOf(FakeChildAggregate.class));
    }

    @Test
    public void canSaveFromParentMapper() {
        FakeChildAggregate fakeChildEntity = new FakeChildAggregate();
        fakeChildEntity.setChildName("test");

        DBObject dbObject = context.mapperFor(FakeAggregate.class).toDBObject(fakeChildEntity);

        assertThat((String) dbObject.get("__discriminator"), is("FakeChildAggregate"));
    }

    @Test
    public void canPopulateRestrictionForAllSubtypes() {
        AggregateMapper<FakeAggregate> mapper = (AggregateMapper<FakeAggregate>) context.mapperFor(FakeAggregate.class);
        Criteria criteria = new Criteria(mock(QueryExecutor.class));

        mapper.applyRestrictionsFor(FakeAggregate.class, criteria);

        DBObject query = criteria.createQuery();
        assertThat(query.get("$or"), notNullValue());
        BasicDBList or = (BasicDBList) query.get("$or");
        assertThat(or.size(), is(2));
    }

    @Test
    public void canPopulateRestrictionForAGivenSubtype() {
        AggregateMapper<FakeAggregate> mapper = (AggregateMapper<FakeAggregate>) context.mapperFor(FakeAggregate.class);
        Criteria criteria = new Criteria(mock(QueryExecutor.class));

        mapper.applyRestrictionsFor(FakeChildAggregate.class, criteria);

        DBObject query = criteria.createQuery();
        assertThat(query.get("$or"), notNullValue());
        BasicDBList or = (BasicDBList) query.get("$or");
        assertThat(or.size(), is(1));
    }


    private void createContext() {
        SubclassMap<FakeChildAggregate> subclassMap = new SubclassMap<FakeChildAggregate>() {

            @Override
            public void map() {
                property().onProperty(element().getChildName());
            }
        };

        FakeAggregateMapping fakeEntityMapping = new FakeAggregateMapping();
        fakeEntityMapping.subclass(subclassMap);
        context = new MapperContext();
        fakeEntityMapping.buildMapper(context);
        parrentMapper = fakeEntityMapping.getMapper();
    }

    private MapperContext context;
    private AggregateMapper<FakeAggregate> parrentMapper;
}
