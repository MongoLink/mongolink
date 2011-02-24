package fr.bodysplash.mongomapper.mapper;

import com.mongodb.DBObject;
import fr.bodysplash.mongomapper.test.FakeEntity;
import fr.bodysplash.mongomapper.test.FakeEntityMapping;
import org.bson.types.ObjectId;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class TestsClassMap {

    @Test
    public void canBuildMapper() {
        FakeEntityMapping mapping = new FakeEntityMapping();
        MapperContext context = new MapperContext();

        mapping.buildMapper(context);

        assertThat(context.mapperFor(FakeEntity.class), notNullValue());
    }

    @Test
    public void canMapId() {
        FakeEntity entity = new FakeEntity("test.com");
        entity.setId("4d53b7118653a70549fe1b78");

        Mapper<FakeEntity> mapper = entityMapper();
        DBObject dbObject = mapper.toDBObject(entity);

        assertThat(dbObject.get("_id"), is((Object) new ObjectId("4d53b7118653a70549fe1b78")));
    }

    private Mapper<FakeEntity> entityMapper() {
        InnerFakeEntityMapping mapping = new InnerFakeEntityMapping();
        MapperContext context = new MapperContext();
        mapping.buildMapper(context);
        return context.mapperFor(FakeEntity.class);
    }

    private static class InnerFakeEntityMapping extends ClassMap<FakeEntity>{

        InnerFakeEntityMapping() {
            super(FakeEntity.class);
        }

        @Override
        protected void map() {
            id(element().getId());
        }

    }
}
