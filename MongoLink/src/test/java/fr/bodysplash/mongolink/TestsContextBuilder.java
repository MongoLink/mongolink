package fr.bodysplash.mongolink;

import fr.bodysplash.mongolink.domain.mapper.ContextBuilder;
import fr.bodysplash.mongolink.domain.mapper.Mapper;
import fr.bodysplash.mongolink.domain.mapper.MapperContext;
import fr.bodysplash.mongolink.test.entity.Comment;
import fr.bodysplash.mongolink.test.entity.FakeChildEntity;
import fr.bodysplash.mongolink.test.entity.FakeEntity;
import fr.bodysplash.mongolink.test.entity.FakeEntityWithNaturalId;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class TestsContextBuilder {

    @Test
    public void canLoadMappingFromPackage() {
        ContextBuilder builder = new ContextBuilder("fr.bodysplash.mongolink.test.simpleMapping");

        MapperContext context = builder.createContext();

        assertThat(context.mapperFor(FakeEntity.class), notNullValue());
        assertThat(context.mapperFor(FakeEntityWithNaturalId.class), notNullValue());
        assertThat(context.mapperFor(Comment.class), notNullValue());
    }

    @Test
    public void dontLoadSubclassMap() {
        ContextBuilder builder = new ContextBuilder("fr.bodysplash.mongolink.test.inheritanceMapping");

        MapperContext context = builder.createContext();

        Mapper mapper = context.mapperFor(FakeChildEntity.class);
        assertThat(mapper, is((Mapper)context.mapperFor(FakeEntity.class)));
    }
}
