package fr.bodysplash.mongolink;

import fr.bodysplash.mongolink.domain.mapper.*;
import fr.bodysplash.mongolink.test.entity.*;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;

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
        assertThat(mapper, is((Mapper) context.mapperFor(FakeEntity.class)));
    }
}
