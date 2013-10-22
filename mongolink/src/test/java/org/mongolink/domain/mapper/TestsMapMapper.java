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

import com.google.common.collect.Maps;
import com.mongodb.BasicDBObject;
import org.junit.Test;
import org.mongolink.utils.FieldContainer;

import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class TestsMapMapper {

    @Test
    public void canSaveStringHashmap() throws NoSuchMethodException {
        final MapMapper mapMapper = createMapper();
        final EntityWithHashmap entity = new EntityWithHashmap();
        entity.values.put("testkey", "testvalue");
        final BasicDBObject into = new BasicDBObject();

        mapMapper.save(entity, into);

        assertThat(into.get("values"), notNullValue());
    }

    @Test
    public void canPopulateStringHashmap() throws NoSuchMethodException {
        final MapMapper mapMapper = createMapper();
        final BasicDBObject basicDBObject = new BasicDBObject();
        Map<String, String> values = Maps.newHashMap();
        values.put("testkey", "testvalue");
        basicDBObject.put("values", values);
        final EntityWithHashmap entity = new EntityWithHashmap();

        mapMapper.populate(entity, basicDBObject);

        assertThat(entity.getValues().size(), is(1));
        assertThat(entity.getValues().get("testkey"), is("testvalue"));
    }

    private MapMapper createMapper() throws NoSuchMethodException {
        final MapMapper result = new MapMapper(new FieldContainer(EntityWithHashmap.class.getMethod("getValues", null)));
        final MapperContext context = new MapperContext();
        final ClassMapper classMapper = mock(ClassMapper.class);
        when(classMapper.getContext()).thenReturn(context);
        return result;
    }

    public class EntityWithHashmap {

        public Map<String, String> getValues() {
            return values;
        }

        Map<String, String> values = Maps.newHashMap();
    }
}
