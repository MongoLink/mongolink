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

import org.junit.Test;
import org.mongolink.domain.mapper.*;
import org.mongolink.test.entity.*;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class TestsContextBuilder {

    @Test
    public void canLoadMappingFromPackage() {
        ContextBuilder builder = new ContextBuilder("org.mongolink.test.simpleMapping");

        MapperContext context = builder.createContext();

        assertThat(context.mapperFor(FakeAggregate.class), notNullValue());
        assertThat(context.mapperFor(FakeAggregateWithNaturalId.class), notNullValue());
        assertThat(context.mapperFor(Comment.class), notNullValue());
    }

    @Test
    public void canLoadMappingFromSomePackages() {
        ContextBuilder builder = new ContextBuilder("org.mongolink.test.simpleMapping", "org.mongolink.test.additionalMapping");

        MapperContext context = builder.createContext();

        assertThat(context.mapperFor(FakeAggregate.class), notNullValue());
        assertThat(context.mapperFor(FakeAggregateWithNaturalId.class), notNullValue());
        assertThat(context.mapperFor(Comment.class), notNullValue());
        assertThat(context.mapperFor(Post.class), notNullValue());
    }

    @Test
    public void dontLoadSubclassMap() {
        ContextBuilder builder = new ContextBuilder("org.mongolink.test.inheritanceMapping");

        MapperContext context = builder.createContext();

        ClassMapper mapper = context.mapperFor(FakeChildAggregate.class);
        assertThat(mapper, is((ClassMapper) context.mapperFor(FakeAggregate.class)));
    }
}
