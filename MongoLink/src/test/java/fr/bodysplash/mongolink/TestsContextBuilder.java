package fr.bodysplash.mongolink;

import fr.bodysplash.mongolink.mapper.ContextBuilder;
import fr.bodysplash.mongolink.mapper.MapperContext;
import fr.bodysplash.mongolink.test.Comment;
import fr.bodysplash.mongolink.test.FakeEntity;
import fr.bodysplash.mongolink.test.FakeEntityWithNaturalId;
import org.junit.Test;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class TestsContextBuilder {

    @Test
    public void canLoadMappingFromPackage() {
        ContextBuilder builder = new ContextBuilder("fr.bodysplash.mongolink.test");

        MapperContext context = builder.createContext();

        assertThat(context.mapperFor(FakeEntity.class), notNullValue());
        assertThat(context.mapperFor(FakeEntityWithNaturalId.class), notNullValue());
        assertThat(context.mapperFor(Comment.class), notNullValue());
    }
}
