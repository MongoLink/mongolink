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

import com.google.common.collect.Lists;
import com.mongodb.*;
import org.junit.Test;
import org.mongolink.utils.FieldContainer;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class TestsCollectionMapper {

    @Test
    public void canSaveStringCollection() throws NoSuchMethodException {
        final CollectionMapper collectionMapper = createMapper();
        final EntityWithCollection entity = new EntityWithCollection();
        entity.list.add("test");
        final BasicDBObject into = new BasicDBObject();

        collectionMapper.save(entity, into);

        assertThat(into.get("list"), notNullValue());
    }

    @Test
    public void canPopulateStringCollection() throws NoSuchMethodException {
        final CollectionMapper mapper = createMapper();
        final BasicDBObject dbObject = dbObjectContaining("test");
        final EntityWithCollection entity = new EntityWithCollection();

        mapper.populate(entity, dbObject);

        assertThat(entity.getList().size(), is(1));
        assertThat(entity.getList().get(0), is("test"));
    }

    private BasicDBObject dbObjectContaining(String value) {
        final BasicDBObject dbObject = new BasicDBObject();
        final BasicDBList objects = new BasicDBList();
        objects.add(value);
        dbObject.put("list", objects);
        return dbObject;
    }

    private CollectionMapper createMapper() throws NoSuchMethodException {
        final CollectionMapper result = new CollectionMapper(new FieldContainer(EntityWithCollection.class.getMethod("getList", null)));
        final MapperContext context = new MapperContext();
        final ClassMapper classMapper = mock(ClassMapper.class);
        when(classMapper.getContext()).thenReturn(context);
        result.setMapper(classMapper);
        return result;
    }

    public class EntityWithCollection {

        public List<String> getList() {
            return list;
        }

        private final List<String> list = Lists.newArrayList();
    }
}
