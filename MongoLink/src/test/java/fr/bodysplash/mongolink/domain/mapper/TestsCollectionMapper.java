package fr.bodysplash.mongolink.domain.mapper;

import com.google.common.collect.Lists;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import fr.bodysplash.mongolink.utils.MethodContainer;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
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
        final CollectionMapper result = new CollectionMapper(new MethodContainer(EntityWithCollection.class.getMethod("getList", null)));
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

        private List<String> list = Lists.newArrayList();
    }
}
