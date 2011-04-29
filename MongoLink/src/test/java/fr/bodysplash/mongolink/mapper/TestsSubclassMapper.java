package fr.bodysplash.mongolink.mapper;


import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import fr.bodysplash.mongolink.test.entity.FakeChildEntity;
import fr.bodysplash.mongolink.test.inheritanceMapping.FakeEntityWithSubclassMapping;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class TestsSubclassMapper {

    @Before
    public void before() {
        createContext();
    }

    @Test
    public void canSaveSubclass() {
        FakeChildEntity entity = new FakeChildEntity();
        entity.setValue("this is a value");
        entity.setId("good id");
        entity.setChildName("this is a name");

        DBObject dbObject = context.mapperFor(FakeChildEntity.class).toDBObject(entity);

        assertThat(dbObject, notNullValue());
        assertThat((String) dbObject.get("value"), is("this is a value"));
        assertThat((String) dbObject.get("_id"), is("good id"));
        assertThat((String) dbObject.get("childName"), is("this is a name"));
        assertThat((String) dbObject.get("__discriminator"), is("FakeChildEntity"));
    }

    @Test
    public void canPopulateFromDb() {
        BasicDBObject dbo = new BasicDBObject();
        dbo.put("_id", "good id");
        dbo.put("value", "this is a value");
        dbo.put("childName", "this is a name");

        FakeChildEntity entity = context.mapperFor(FakeChildEntity.class).toInstance(dbo);

        assertThat(entity, notNullValue());
        assertThat(entity.getId(), is("good id"));
        assertThat(entity.getValue(), is("this is a value"));
        assertThat(entity.getChildName(), is("this is a name"));
    }

    private void createContext() {
        FakeEntityWithSubclassMapping mapping = new FakeEntityWithSubclassMapping();
        context = new MapperContext();
        mapping.buildMapper(context);
    }

    private MapperContext context;
}
