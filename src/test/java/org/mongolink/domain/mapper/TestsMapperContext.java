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
import org.mongolink.domain.converter.Converter;
import org.mongolink.test.entity.Comment;
import org.mongolink.test.simpleMapping.CommentMapping;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class TestsMapperContext {

    @Test
    public void canGetConverterForBasicType() {
        final MapperContext context = new MapperContext();

        Converter converter = context.converterFor(String.class);

        assertThat(converter, notNullValue());
    }

    @Test
    public void canGetConverterForComponent() {
        final MapperContext context = new MapperContext();
        final CommentMapping mapping = new CommentMapping();
        mapping.buildMapper(context);

        final Converter converter = context.converterFor(Comment.class);

        assertThat(converter, notNullValue());
        assertThat(converter, instanceOf(ComponentMapper.class));
    }

}
