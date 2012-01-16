package fr.bodysplash.mongolink.domain.mapper;

import com.mongodb.DBObject;
import fr.bodysplash.mongolink.test.entity.Comment;
import fr.bodysplash.mongolink.test.entity.FakeEntity;
import fr.bodysplash.mongolink.test.simpleMapping.CommentMapping;
import fr.bodysplash.mongolink.test.simpleMapping.FakeEntityMapping;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class TestsClassMapper {

    @Before
    public void before() {
        FakeEntityMapping fakeEntityMapping = new FakeEntityMapping();
        CommentMapping commentMapping = new CommentMapping();
        context = new MapperContext();
        fakeEntityMapping.buildMapper(context);
        commentMapping.buildMapper(context);
    }

    @Test
    public void canSaveComponent() {
        FakeEntity entity = new FakeEntity("ok");
        entity.setComment(new Comment("valeur"));

        final DBObject dbObject = entityMapper().toDBObject(entity);

        assertThat(dbObject.get("comment"), notNullValue());
    }

    private EntityMapper<FakeEntity> entityMapper() {
        return (EntityMapper<FakeEntity>) context.mapperFor(FakeEntity.class);
    }

    private MapperContext context;
}
