package fr.bodysplash.mongolink.domain.mapper;

import fr.bodysplash.mongolink.domain.mapper.*;
import fr.bodysplash.mongolink.test.entity.FakeChildEntity;
import fr.bodysplash.mongolink.test.entity.FakeEntity;
import fr.bodysplash.mongolink.test.entity.OtherFakeChildEntity;
import fr.bodysplash.mongolink.test.inheritanceMapping.FakeEntityWithSubclassMapping;
import fr.bodysplash.mongolink.test.inheritanceMapping.FakeEntityWithTwoSubclassMapping;
import fr.bodysplash.mongolink.test.simpleMapping.FakeEntityMapping;
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



}
