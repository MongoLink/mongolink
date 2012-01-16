package fr.bodysplash.mongolink.domain.mapper;

import com.mongodb.BasicDBObject;
import fr.bodysplash.mongolink.test.entity.Comment;
import fr.bodysplash.mongolink.test.entity.FakeEntity;
import fr.bodysplash.mongolink.test.simpleMapping.CommentMapping;
import fr.bodysplash.mongolink.utils.MethodContainer;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class TestsPropertyComponentMapper {
    
    @Before
    public void setUp() throws NoSuchMethodException {
        MapperContext context = new MapperContext();
        CommentMapping mapping = new CommentMapping();
        mapping.buildMapper(context);
        final ClassMapper classMapper = mock(ClassMapper.class);
        when(classMapper.getContext()).thenReturn(context);
        when(classMapper.getPersistentType()).thenReturn(FakeEntity.class);
        final Method method = FakeEntity.class.getMethod("getComment", null);
        methodContainer = new MethodContainer(method);
        propertyComponentMapper = new PropertyComponentMapper(Comment.class, methodContainer);
        propertyComponentMapper.setMapper(classMapper);

    }

    @Test
    public void canFindComponent() {
        ComponentMapper mapper = propertyComponentMapper.find();

        assertThat(mapper, notNullValue());
    }

    @Test
    public void canSerializeToDBOject() throws InvocationTargetException, IllegalAccessException {
        final BasicDBObject into = new BasicDBObject();
        FakeEntity entity = new FakeEntity("te");
        final Comment comment = new Comment("tes");
        entity.setComment(comment);

        propertyComponentMapper.save(entity, into);

        final BasicDBObject commentDB = (BasicDBObject) into.get("comment");
        assertThat(commentDB, notNullValue());
        assertThat(commentDB.get("value").toString(), is("tes"));
    }

    @Test
    public void canPopulate() throws NoSuchMethodException {
        final BasicDBObject from = new BasicDBObject();
        final BasicDBObject val = new BasicDBObject();
        val.put("value", "valeur");
        from.put("comment", val);
        FakeEntity instance = new FakeEntity("kjklj");
                
        propertyComponentMapper.populate(instance, from);
        
        assertThat(instance.getComment().getValue(), is("valeur"));
    }

    private PropertyComponentMapper propertyComponentMapper;
    private MethodContainer methodContainer;
}
