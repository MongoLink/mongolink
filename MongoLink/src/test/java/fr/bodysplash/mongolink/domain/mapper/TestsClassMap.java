package fr.bodysplash.mongolink.domain.mapper;

import fr.bodysplash.mongolink.test.entity.*;
import fr.bodysplash.mongolink.test.inheritanceMapping.*;
import fr.bodysplash.mongolink.test.simpleMapping.*;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

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

    @Test
    public void subclassIsAddedToTheContext() {
        FakeEntityWithSubclassMapping mapping = new FakeEntityWithSubclassMapping();
        MapperContext context = new MapperContext();

        mapping.buildMapper(context);

        assertThat(context.mapperFor(FakeChildEntity.class), notNullValue());
    }

    @Test
    public void canHaveSeveralSubclasses() {
        FakeEntityWithTwoSubclassMapping mapping = new FakeEntityWithTwoSubclassMapping();
        MapperContext context = new MapperContext();

        mapping.buildMapper(context);

        assertThat(context.mapperFor(FakeChildEntity.class), notNullValue());
        assertThat(context.mapperFor(OtherFakeChildEntity.class), notNullValue());
    }

    @Test
    public void canSetCap() {
        FakeEntityMappingWithCap mapping = new FakeEntityMappingWithCap();
        MapperContext context = new MapperContext();

        mapping.buildMapper(context);

        assertThat(context.mapperFor(FakeEntityWithCap.class).isCapped(), is(true));
    }

    @Test
    public void canSetCapSize() {
        FakeEntityMappingWithCap mapping = new FakeEntityMappingWithCap();
        MapperContext context = new MapperContext();

        mapping.buildMapper(context);

        EntityMapper<?> entityMapper = (EntityMapper<?>) context.mapperFor(FakeEntityWithCap.class);
        assertThat(entityMapper.getCappedSize(), is(1048076));
    }

    @Test
    public void canSetCapMax() {
        FakeEntityMappingWithCap mapping = new FakeEntityMappingWithCap();
        MapperContext context = new MapperContext();

        mapping.buildMapper(context);

        EntityMapper<?> entityMapper = (EntityMapper<?>) context.mapperFor(FakeEntityWithCap.class);
        assertThat(entityMapper.getCappedMax(), is(50));
    }
}
