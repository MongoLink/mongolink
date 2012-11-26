package org.mongolink.domain.mapper;

import com.google.common.collect.Maps;
import com.mongodb.BasicDBObject;
import org.junit.Test;
import org.mongolink.utils.MethodContainer;

import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class TestsHashmapMapper {

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
        final MapMapper result = new MapMapper(new MethodContainer(EntityWithHashmap.class.getMethod("getValues", null)));
        final MapperContext context = new MapperContext();
        final ClassMapper classMapper = mock(ClassMapper.class);
        when(classMapper.getContext()).thenReturn(context);
        result.setMapper(classMapper);
        return result;
    }

    public class EntityWithHashmap {

        public Map<String, String> getValues() {
            return values;
        }

        Map<String, String> values = Maps.newHashMap();
    }
}
