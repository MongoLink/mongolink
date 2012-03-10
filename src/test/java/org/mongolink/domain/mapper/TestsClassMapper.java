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

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.junit.Before;
import org.junit.Test;
import org.mongolink.test.entity.Comment;
import org.mongolink.test.entity.FakeEntity;
import org.mongolink.test.simpleMapping.CommentMapping;
import org.mongolink.test.simpleMapping.FakeEntityMapping;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class TestsClassMapper {

    @Before
    public void before() {
        FakeEntityMapping fakeEntityMapping = new FakeEntityMapping();
        CommentMapping commentMapping = new CommentMapping();
        context = new MapperContext();
        fakeEntityMapping.buildMapper(context);
        commentMapping.buildMapper(context);
    }

    @Test
    public void canSaveComponent() {
        FakeEntity entity = new FakeEntity("ok");
        entity.setComment(new Comment("valeur"));

        final DBObject dbObject = entityMapper().toDBObject(entity);

        assertThat(dbObject.get("comment"), notNullValue());
    }

    @Test
    public void canConvertFromDBValue() {
        final Object value = entityMapper().toDbValue(new FakeEntity("ok"));

        assertThat(value, instanceOf(DBObject.class));
    }

    @Test
    public void canCreateInstanceFromDBValue() {
        final BasicDBObject value = new BasicDBObject();
        value.put("value", "test");

        final Object instance = entityMapper().fromDbValue(value);

        assertThat(instance, instanceOf(FakeEntity.class));
        assertThat(((FakeEntity) instance).getValue(), is("test"));
    }

    private EntityMapper<FakeEntity> entityMapper() {
        return (EntityMapper<FakeEntity>) context.mapperFor(FakeEntity.class);
    }

    private MapperContext context;
}
