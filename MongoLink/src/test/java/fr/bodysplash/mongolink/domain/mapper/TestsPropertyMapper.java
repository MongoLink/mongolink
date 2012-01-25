package fr.bodysplash.mongolink.domain.mapper;

import com.mongodb.BasicDBObject;
import fr.bodysplash.mongolink.test.entity.Comment;
import fr.bodysplash.mongolink.test.simpleMapping.CommentMapping;
import fr.bodysplash.mongolink.utils.MethodContainer;
import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class TestsPropertyMapper {

    public enum TestEnum {
        good, bad
    }

    public static class FakeEntity {
        private TestEnum value;
        private int primitive;
        private DateTime creationDate;

        public DateTime getCreationDate() {
            return creationDate;
        }

        public void setCreationDate(final DateTime creationDate) {
            this.creationDate = creationDate;
        }

        public int getPrimitive() {
            return primitive;
        }

        public TestEnum getValue() {
            return value;
        }
    }

    @Test
    public void canSaveAnEnum() throws NoSuchMethodException {
        PropertyMapper mapper = mapperForEnum();
        FakeEntity entity = new FakeEntity();
        entity.value = TestsPropertyMapper.TestEnum.good;
        BasicDBObject object = new BasicDBObject();

        mapper.save(entity, object);

        assertThat(object.get("value"), is((Object) "good"));
    }

    @Test
    public void canPopulateAnEnum() throws NoSuchMethodException {
        BasicDBObject object = new BasicDBObject();
        object.put("value", "bad");
        PropertyMapper mapper = mapperForEnum();
        FakeEntity instance = new FakeEntity();

        mapper.populate(instance, object);

        assertThat(instance.getValue(), is(TestEnum.bad));
    }

    private PropertyMapper mapperForEnum() throws NoSuchMethodException {
        PropertyMapper propertyMapper = new PropertyMapper(new MethodContainer(FakeEntity.class.getDeclaredMethod("getValue")));
        propertyMapper.setMapper(parentMapper());
        return propertyMapper;
    }

    @Test
    public void canSavePrimitiveType() throws NoSuchMethodException {
        PropertyMapper mapper = mapperForProperty();
        FakeEntity entity = new FakeEntity();
        entity.primitive = 10;
        BasicDBObject object = new BasicDBObject();

        mapper.save(entity, object);

        assertThat(object.get("primitive"), is((Object) 10));
    }

    private PropertyMapper mapperForProperty() throws NoSuchMethodException {
        final PropertyMapper propertyMapper = new PropertyMapper(new MethodContainer(primitiveGetter()));
        propertyMapper.setMapper(parentMapper());
        return propertyMapper;
    }

    private Method primitiveGetter() throws NoSuchMethodException {
        return FakeEntity.class.getDeclaredMethod("getPrimitive");
    }

    @Test
    public void canSaveDateTimeType() throws NoSuchMethodException {
        PropertyMapper mapper = propertyMapperForDate();
        FakeEntity fakeEntity = new FakeEntity();
        DateTime now = new DateTime();
        fakeEntity.setCreationDate(now);
        BasicDBObject basicDBObject = new BasicDBObject();

        mapper.save(fakeEntity, basicDBObject);

        assertThat(basicDBObject.get("creationDate"), is((Object) now.getMillis()));
    }

    @Test
    public void canPopulateDateTimeType() throws NoSuchMethodException {
        BasicDBObject object = new BasicDBObject();
        DateTime dateTime = new DateTime();
        object.put("creationDate", dateTime.getMillis());
        PropertyMapper propertyMapper = propertyMapperForDate();
        FakeEntity instance = new FakeEntity();

        propertyMapper.populate(instance, object);

        assertThat(instance.getCreationDate(), is(dateTime));
    }

    private PropertyMapper propertyMapperForDate() throws NoSuchMethodException {
        final PropertyMapper result = new PropertyMapper(new MethodContainer(FakeEntity.class.getDeclaredMethod("getCreationDate")));
        result.setMapper(parentMapper());
        return result;
    }

    private EntityMapper<FakeEntity> parentMapper() {
        EntityMapper<FakeEntity> mapper = new EntityMapper<FakeEntity>(FakeEntity.class);
        final MapperContext context = new MapperContext();
        context.addMapper(mapper);
        return mapper;
    }

    @Test
    public void canSerializeToDBOject() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        final BasicDBObject into = new BasicDBObject();
        fr.bodysplash.mongolink.test.entity.FakeEntity entity = new fr.bodysplash.mongolink.test.entity.FakeEntity("te");
        final Comment comment = new Comment("tes");
        entity.setComment(comment);

        propertyMapperForComponent().save(entity, into);

        final BasicDBObject commentDB = (BasicDBObject) into.get("comment");
        assertThat(commentDB, notNullValue());
        assertThat(commentDB.get("value").toString(), Matchers.is("tes"));
    }

    @Test
    public void canPopulate() throws NoSuchMethodException {
        final BasicDBObject from = new BasicDBObject();
        final BasicDBObject val = new BasicDBObject();
        val.put("value", "valeur");
        from.put("comment", val);
        fr.bodysplash.mongolink.test.entity.FakeEntity instance = new fr.bodysplash.mongolink.test.entity.FakeEntity("kjklj");

        propertyMapperForComponent().populate(instance, from);

        assertThat(instance.getComment().getValue(), Matchers.is("valeur"));
    }


    public PropertyMapper propertyMapperForComponent() throws NoSuchMethodException {
        MapperContext context = new MapperContext();
        CommentMapping mapping = new CommentMapping();
        mapping.buildMapper(context);
        final ClassMapper classMapper = mock(ClassMapper.class);
        when(classMapper.getContext()).thenReturn(context);
        when(classMapper.getPersistentType()).thenReturn(fr.bodysplash.mongolink.test.entity.FakeEntity.class);
        final Method method = fr.bodysplash.mongolink.test.entity.FakeEntity.class.getMethod("getComment", null);
        MethodContainer methodContainer = new MethodContainer(method);
        PropertyMapper propertyComponentMapper = new PropertyMapper(methodContainer);
        propertyComponentMapper.setMapper(classMapper);
        return propertyComponentMapper;
    }

}
