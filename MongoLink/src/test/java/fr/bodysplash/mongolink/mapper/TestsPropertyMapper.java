package fr.bodysplash.mongolink.mapper;

import com.mongodb.BasicDBObject;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class TestsPropertyMapper {

    public enum TestEnum {
        good, bad
    }

    public static class FakeEntity {
        private TestEnum value;
        private int primitive;

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
        PropertyMapper propertyMapper = new PropertyMapper("value", FakeEntity.class.getDeclaredMethod("getValue"));
        Mapper<FakeEntity> mapper = new Mapper<FakeEntity>(FakeEntity.class);
        propertyMapper.setMapper(mapper);
        return propertyMapper;
    }

    @Test
    public void canSavePrimitiveType() throws NoSuchMethodException {
        PropertyMapper mapper = new PropertyMapper("primitive", primitiveGetter());
        FakeEntity entity = new FakeEntity();
        entity.primitive = 10;
        BasicDBObject object = new BasicDBObject();

        mapper.saveTo(entity, object);

        assertThat(object.get("primitive"), is((Object) 10));
    }

    private Method primitiveGetter() throws NoSuchMethodException {
        return FakeEntity.class.getDeclaredMethod("getPrimitive");
    }
}
