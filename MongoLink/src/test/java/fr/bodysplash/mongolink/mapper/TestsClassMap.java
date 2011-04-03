package fr.bodysplash.mongolink.mapper;

import fr.bodysplash.mongolink.test.FakeEntity;
import fr.bodysplash.mongolink.test.FakeEntityMapping;
import org.junit.Test;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class TestsClassMap {

    @Test
    public void canBuildMapper() {
        FakeEntityMapping mapping = new FakeEntityMapping();
        MapperContext context = new MapperContext();

        mapping.buildMapper(context);

        assertThat(context.mapperFor(FakeEntity.class), notNullValue());
        Mapper<FakeEntity> mapper = context.mapperFor(FakeEntity.class);

    }

    @Test
    public void providesInterceptor() {
        FakeEntityMapping mapping = new FakeEntityMapping();

        assertThat(mapping.element(), notNullValue());
    }

}
