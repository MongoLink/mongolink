package fr.bodysplash.mongolink.domain.mapper;

import fr.bodysplash.mongolink.domain.converter.Converter;
import fr.bodysplash.mongolink.test.entity.Comment;
import fr.bodysplash.mongolink.test.simpleMapping.CommentMapping;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class TestsMapperContext {

    @Test
    public void canGetConverterForBasicType() {
        final MapperContext context = new MapperContext();
        
        Converter converter = context.converterFor(String.class);

        assertThat(converter, notNullValue());
    }

    @Test
    public void canGetConverterForComponent() {
        final MapperContext context = new MapperContext();
        final CommentMapping mapping = new CommentMapping();
        mapping.buildMapper(context);

        final Converter converter = context.converterFor(Comment.class);

        assertThat(converter, notNullValue());
        assertThat(converter, instanceOf(ComponentMapper.class));
    }

}
