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

import com.mongodb.*;
import org.bson.types.ObjectId;
import org.hamcrest.Matchers;
import org.junit.*;
import org.mongolink.test.entity.*;
import org.mongolink.test.inheritanceMapping.FakeAggregateWithSubclassMapping;
import org.mongolink.test.simpleMapping.*;

import java.net.UnknownHostException;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class TestsAggregateMapper {


    @Before
    public void before() throws UnknownHostException {
        FakeAggregateMapping fakeEntityMapping = new FakeAggregateMapping();
        CommentMapping commentMapping = new CommentMapping();
        context = new MapperContext();
        fakeEntityMapping.buildMapper(context);
        commentMapping.buildMapper(context);
    }

    @Test
    public void canSaveAutoId() {
        FakeAggregate entity = new FakeAggregate("test.com");
        entity.setId("4d53b7118653a70549fe1b78");

        DBObject dbObject = entityMapper().toDBObject(entity);

        Assert.assertThat(dbObject.get("_id"), Matchers.is((Object) new ObjectId("4d53b7118653a70549fe1b78")));
    }

    @Test
    public void canSaveNaturalId() {
        MapperContext mapperContext = contextWithNaturalId();
        FakeAggregateWithNaturalId entity = new FakeAggregateWithNaturalId("natural key");

        DBObject dbObject = mapperContext.mapperFor(FakeAggregateWithNaturalId.class).toDBObject(entity);

        Assert.assertThat(dbObject.get("_id"), Matchers.is((Object) "natural key"));
    }

    @Test
    public void canPopulateNaturalId() {
        MapperContext mapperContext = contextWithNaturalId();
        DBObject dbo = new BasicDBObject();
        dbo.put("_id", "natural key");

        FakeAggregateWithNaturalId instance = mapperContext.mapperFor(FakeAggregateWithNaturalId.class).toInstance(dbo);

        assertThat(instance.getNaturalKey(), is((Object) "natural key"));
    }

    private MapperContext contextWithNaturalId() {
        FakeAggregateWithNaturalIdMapping mapping = new FakeAggregateWithNaturalIdMapping();
        MapperContext mapperContext = new MapperContext();
        mapping.buildMapper(mapperContext);
        return mapperContext;
    }


    @Test
    public void canSaveProperties() {
        FakeAggregate entity = new FakeAggregate("test.com");


        DBObject dbo = entityMapper().toDBObject(entity);

        Assert.assertThat(dbo, Matchers.notNullValue());
        Assert.assertThat(dbo.get("value"), Matchers.is((Object) "test.com"));

    }

    @Test
    public void canSaveCollectionOfComponent() {
        FakeAggregate entity = new FakeAggregate("test.com");
        entity.addComment("un commentaire");

        DBObject dbo = entityMapper().toDBObject(entity);

        Assert.assertThat(dbo, Matchers.notNullValue());
        Assert.assertThat(dbo.get("comments"), Matchers.notNullValue());
        BasicDBList comments = (BasicDBList) dbo.get("comments");
        Assert.assertThat(comments.size(), Matchers.is(1));
        DBObject comment = (DBObject) comments.get(0);
        Assert.assertThat(comment.get("value"), Matchers.is((Object) "un commentaire"));

    }

    @Test
    public void canPopulateProperties() {
        DBObject dbo = new BasicDBObject();
        dbo.put("value", "test.com");
        dbo.put("_id", "id");

        FakeAggregate entity = entityMapper().toInstance(dbo);

        Assert.assertThat(entity, Matchers.notNullValue());
        Assert.assertThat(entity.getValue(), Matchers.is("test.com"));
        Assert.assertThat(entity.getId(), Matchers.is("id"));
    }

    @Test
    public void canPopulateCollection() {
        DBObject dbo = new BasicDBObject();
        dbo.put("url", "test.com");
        dbo.put("_id", "id");
        BasicDBList comments = new BasicDBList();
        DBObject comment = new BasicDBObject();
        comment.put("value", "this is a mapper!");
        comments.add(comment);
        dbo.put("comments", comments);

        FakeAggregate entity = entityMapper().toInstance(dbo);

        assertThat(entity.getComments().size(), is(1));
        Assert.assertThat(entity.getComments().get(0).getValue(), Matchers.is("this is a mapper!"));
    }

    @Test
    public void canCreateInstanceGivenDiscriminatorValue() {
        MapperContext context = new MapperContext();
        new FakeAggregateWithSubclassMapping().buildMapper(context);
        ClassMapper<FakeAggregate> entityMapper = context.mapperFor(FakeAggregate.class);
        BasicDBObject dbo = new BasicDBObject();
        dbo.put("_id", "1");
        dbo.put("__discriminator", "FakeChildAggregate");

        FakeAggregate instance = entityMapper.toInstance(dbo);

        assertThat(instance, notNullValue());
        assertThat(instance, instanceOf(FakeChildAggregate.class));

    }

    @Test
    public void createParentEntityWhenNoDiscriminatorGiven() {
        MapperContext context = new MapperContext();
        new FakeAggregateWithSubclassMapping().buildMapper(context);
        ClassMapper<FakeAggregate> entityMapper = context.mapperFor(FakeAggregate.class);
        BasicDBObject dbo = new BasicDBObject();
        dbo.put("_id", "1");

        FakeAggregate instance = entityMapper.toInstance(dbo);

        assertThat(instance, notNullValue());
        assertThat(instance, instanceOf(FakeAggregate.class));
    }


    private AggregateMapper<FakeAggregate> entityMapper() {
        return (AggregateMapper<FakeAggregate>) context.mapperFor(FakeAggregate.class);
    }

    private MapperContext context;

}
