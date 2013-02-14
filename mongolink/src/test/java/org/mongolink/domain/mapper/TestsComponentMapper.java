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
import org.hamcrest.Matchers;
import org.junit.*;
import org.mongolink.test.entity.Comment;
import org.mongolink.test.simpleMapping.CommentMapping;

import java.net.UnknownHostException;

public class TestsComponentMapper {

    @Before
    public void before() throws UnknownHostException {
        CommentMapping commentMapping = new CommentMapping();
        MapperContext context = new MapperContext();
        commentMapping.buildMapper(context);
        mapper = (ComponentMapper<Comment>) context.mapperFor(Comment.class);
    }

    @Test
    public void canSaveProperties() {
        Comment comment = new Comment("the value");

        DBObject dbo = mapper.toDBObject(comment);

        Assert.assertThat(dbo, Matchers.notNullValue());
        Assert.assertThat(dbo.get("value"), Matchers.is((Object) "the value"));

    }

    @Test
    public void canPopulateProperties() {
        DBObject dbo = new BasicDBObject();
        dbo.put("value", "this is mongolink!");

        Comment entity = mapper.toInstance(dbo);

        Assert.assertThat(entity, Matchers.notNullValue());
        Assert.assertThat(entity.getValue(), Matchers.is("this is mongolink!"));
    }

    private ComponentMapper<Comment> mapper;
}

