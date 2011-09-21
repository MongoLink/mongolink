package fr.bodysplash.mongolink.domain.mapper;

import com.mongodb.*;
import fr.bodysplash.mongolink.test.entity.Comment;
import fr.bodysplash.mongolink.test.simpleMapping.CommentMapping;
import org.hamcrest.Matchers;
import org.junit.*;

import java.net.UnknownHostException;

public class TestsComponentMapper {

    @Before
    public void before() throws UnknownHostException {
        CommentMapping commentMapping = new CommentMapping();
        MapperContext context = new MapperContext();
        commentMapping.buildMapper(context);
        mapper = (ComponentMapper<Comment>) context.mapperFor(Comment.class);
    }

    @Test
    public void canSaveProperties() {
        Comment comment = new Comment("the value");

        DBObject dbo = mapper.toDBObject(comment);

        Assert.assertThat(dbo, Matchers.notNullValue());
        Assert.assertThat(dbo.get("value"), Matchers.is((Object) "the value"));

    }

    @Test
    public void canPopulateProperties() {
        DBObject dbo = new BasicDBObject();
        dbo.put("value", "this is mongolink!");

        Comment entity = mapper.toInstance(dbo);

        Assert.assertThat(entity, Matchers.notNullValue());
        Assert.assertThat(entity.getValue(), Matchers.is("this is mongolink!"));
    }

    private ComponentMapper<Comment> mapper;
}

