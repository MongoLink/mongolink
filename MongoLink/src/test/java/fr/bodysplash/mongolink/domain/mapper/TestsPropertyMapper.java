package fr.bodysplash.mongolink.domain.mapper;

import com.mongodb.BasicDBObject;
import fr.bodysplash.mongolink.utils.MethodContainer;
import org.joda.time.DateTime;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.hamcrest.core.Is.*;
import static org.junit.Assert.*;

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

        mapper.saveTo(entity, object);

        assertThat(object.get("value"), is((Object) "good"));
    }

    @Test
    public void canPopulateAnEnum() throws NoSuchMethodException {
        BasicDBObject object = new BasicDBObject();
        object.put("value", "bad");
        PropertyMapper mapper = mapperForEnum();
        FakeEntity instance = new FakeEntity();

        mapper.populateFrom(instance, object);

        assertThat(instance.getValue(), is(TestEnum.bad));
    }

    private PropertyMapper mapperForEnum() throws NoSuchMethodException {
        PropertyMapper propertyMapper = new PropertyMapper(new MethodContainer(FakeEntity.class.getDeclaredMethod("getValue")));
        EntityMapper<FakeEntity> mapper = new EntityMapper<FakeEntity>(FakeEntity.class);
        propertyMapper.setMapper(mapper);
        return propertyMapper;
    }

    @Test
    public void canSavePrimitiveType() throws NoSuchMethodException {
        PropertyMapper mapper = new PropertyMapper(new MethodContainer(primitiveGetter()));
        FakeEntity entity = new FakeEntity();
        entity.primitive = 10;
        BasicDBObject object = new BasicDBObject();

        mapper.saveTo(entity, object);

        assertThat(object.get("primitive"), is((Object) 10));
    }

    private Method primitiveGetter() throws NoSuchMethodException {
        return FakeEntity.class.getDeclaredMethod("getPrimitive");
    }

    @Test
    public void canSaveDateTimeType() throws NoSuchMethodException {
        PropertyMapper mapper = new PropertyMapper(new MethodContainer(FakeEntity.class.getDeclaredMethod("getCreationDate")));
        FakeEntity fakeEntity = new FakeEntity();
        DateTime now = new DateTime();
        fakeEntity.setCreationDate(now);
        BasicDBObject basicDBObject = new BasicDBObject();

        mapper.saveTo(fakeEntity, basicDBObject);

        assertThat(basicDBObject.get("creationDate"), is((Object) now.getMillis()));
    }

    @Test
    public void canPopulateDateTimeType() throws NoSuchMethodException {
        BasicDBObject object = new BasicDBObject();
        DateTime dateTime = new DateTime();
        object.put("creationDate", dateTime.getMillis());
        PropertyMapper propertyMapper = new PropertyMapper(new MethodContainer(FakeEntity.class.getDeclaredMethod("getCreationDate")));
        EntityMapper<FakeEntity> mapper = new EntityMapper<FakeEntity>(FakeEntity.class);
        propertyMapper.setMapper(mapper);
        FakeEntity instance = new FakeEntity();

        propertyMapper.populateFrom(instance, object);

        assertThat(instance.getCreationDate(), is(dateTime));
    }
}
