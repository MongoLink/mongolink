package fr.bodysplash.mongolink.domain.mapper;


import com.mongodb.*;
import fr.bodysplash.mongolink.test.entity.*;
import fr.bodysplash.mongolink.test.simpleMapping.FakeEntityMapping;
import org.bson.types.ObjectId;
import org.junit.*;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class TestsSubclassMapper {

    @Before
    public void before() {
        createContext();
    }

    @Test
    public void canSaveSubclass() {
        FakeChildEntity entity = new FakeChildEntity();
        entity.setValue("this is a value");
        entity.setId("5d9d9b5e36a9a4265ea9ecbe");
        entity.setChildName("this is a name");

        DBObject dbObject = mapper.toDBObject(entity);

        assertThat(dbObject, notNullValue());
        assertThat((String) dbObject.get("value"), is("this is a value"));
        assertThat(dbObject.get("_id"), is((Object) new ObjectId("5d9d9b5e36a9a4265ea9ecbe")));
        assertThat((String) dbObject.get("__discriminator"), is("FakeChildEntity"));
        assertThat((String) dbObject.get("childName"), is("this is a name"));
    }

    @Test
    public void canPopulateFromDb() {
        BasicDBObject dbo = new BasicDBObject();
        dbo.put("_id", "good id");
        dbo.put("value", "this is a value");
        dbo.put("childName", "this is a name");

        FakeChildEntity entity = mapper.toInstance(dbo);

        assertThat(entity, notNullValue());
        assertThat(entity.getId(), is("good id"));
        assertThat(entity.getValue(), is("this is a value"));
        assertThat(entity.getChildName(), is("this is a name"));
    }

    @Test
    public void canPopulateFromParentMapper() {
        BasicDBObject dbo = new BasicDBObject();
        dbo.put("_id", "good id");
        dbo.put("value", "this is a value");
        dbo.put("childName", "this is a name");
        dbo.put("__discriminator", "FakeChildEntity");

        FakeEntity entity = context.mapperFor(FakeEntity.class).toInstance(dbo);

        assertThat(entity, instanceOf(FakeChildEntity.class));
    }

    @Test
    public void canSaveFromParentMapper() {
        FakeChildEntity fakeChildEntity = new FakeChildEntity();
        fakeChildEntity.setChildName("test");

        DBObject dbObject = context.mapperFor(FakeEntity.class).toDBObject(fakeChildEntity);

        assertThat((String) dbObject.get("__discriminator"), is("FakeChildEntity"));
    }

    private void createContext() {
        SubclassMap<FakeChildEntity> subclassMap = new SubclassMap<FakeChildEntity>(FakeChildEntity.class) {

            @Override
            protected void map() {
                property(element().getChildName());
            }
        };

        FakeEntityMapping fakeEntityMapping = new FakeEntityMapping();
        fakeEntityMapping.subclass(subclassMap);
        context = new MapperContext();
        fakeEntityMapping.buildMapper(context);
        mapper = subclassMap.getMapper();
    }

    private SubclassMapper<FakeChildEntity> mapper;

    private MapperContext context;
}
