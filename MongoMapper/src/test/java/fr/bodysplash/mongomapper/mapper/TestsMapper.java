package fr.bodysplash.mongomapper.mapper;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import fr.bodysplash.mongomapper.test.Comment;
import fr.bodysplash.mongomapper.test.FakeEntity;
import fr.bodysplash.mongomapper.test.FakeEntityWithNaturalId;
import org.bson.types.ObjectId;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.net.UnknownHostException;


public class TestsMapper {


    private MapperContext context;

    @Before
    public void before() throws UnknownHostException {
        ContextBuilder contextBuilder = new ContextBuilder();
        Mapping<FakeEntity> entityMapping = contextBuilder.newMapping(FakeEntity.class);
        entityMapping.id().getId();
        entityMapping.property().getValue();
        entityMapping.collection().getComments();
        Mapping<Comment> commentMapping = contextBuilder.newMapping(Comment.class);
        commentMapping.property().getValue();
        context = contextBuilder.createContext();
    }

    @Test
    public void canSaveAutoId() {
        FakeEntity entity = new FakeEntity("test.com");
        entity.setId("4d53b7118653a70549fe1b78");

        DBObject dbObject = entityMapper().toDBObject(entity);

        Assert.assertThat(dbObject.get("_id"), Matchers.is((Object) new ObjectId("4d53b7118653a70549fe1b78")));
    }

    @Test
    public void canSaveNaturalId() {
        MapperContext mapperContext = contextWithNaturalId();
        FakeEntityWithNaturalId entity = new FakeEntityWithNaturalId("natural key");

        DBObject dbObject = mapperContext.mapperFor(FakeEntityWithNaturalId.class).toDBObject(entity);

        Assert.assertThat(dbObject.get("_id"), Matchers.is((Object) "natural key"));
    }

    @Test
    public void canPopulateNaturalId() {
        MapperContext mapperContext = contextWithNaturalId();
        DBObject dbo = new BasicDBObject();
        dbo.put("_id", "natural key");

        FakeEntityWithNaturalId instance = mapperContext.mapperFor(FakeEntityWithNaturalId.class).toInstance(dbo);

        Assert.assertThat(instance.getNaturalKey(), Matchers.is((Object) "natural key"));
    }


    private MapperContext contextWithNaturalId() {
        ContextBuilder contextBuilder = new ContextBuilder();
        Mapping<FakeEntityWithNaturalId> mapping = contextBuilder.newMapping(FakeEntityWithNaturalId.class);
        mapping.id(IdGeneration.Natural).getNaturalKey();
        return contextBuilder.createContext();
    }

    @Test
    public void canSaveProperties() {
        FakeEntity entity = new FakeEntity("test.com");


        DBObject dbo = entityMapper().toDBObject(entity);

        Assert.assertThat(dbo, Matchers.notNullValue());
        Assert.assertThat(dbo.get("value"), Matchers.is((Object) "test.com"));

    }

    @Test
    public void canSaveCollections() {
        FakeEntity entity = new FakeEntity("test.com");
        entity.addComment("un commentaire");

        DBObject dbo = entityMapper().toDBObject(entity);

        Assert.assertThat(dbo, Matchers.notNullValue());
        Assert.assertThat(dbo.get("comments"), Matchers.notNullValue());
        BasicDBList comments = (BasicDBList) dbo.get("comments");
        Assert.assertThat(comments.size(), Matchers.is(1));
        DBObject comment = (DBObject) comments.get(0);
        Assert.assertThat(comment.get("value"), Matchers.is((Object) "un commentaire"));

    }

    @Test
    public void canPopulateProperties() {
        DBObject dbo = new BasicDBObject();
        dbo.put("value", "test.com");
        dbo.put("_id", "id");

        FakeEntity entity = entityMapper().toInstance(dbo);

        Assert.assertThat(entity, Matchers.notNullValue());
        Assert.assertThat(entity.getValue(), Matchers.is("test.com"));
        Assert.assertThat(entity.getId(), Matchers.is("id"));
    }

    @Test
    public void canPopulateCollection() {
        DBObject dbo = new BasicDBObject();
        dbo.put("url", "test.com");
        dbo.put("_id", "id");
        BasicDBList comments = new BasicDBList();
        DBObject comment = new BasicDBObject();
        comment.put("value", "this is a mapper!");
        comments.add(comment);
        dbo.put("comments", comments);

        FakeEntity entity = entityMapper().toInstance(dbo);

        Assert.assertThat(entity.getComments().size(), Matchers.is(1));
        Assert.assertThat(entity.getComments().get(0).getValue(), Matchers.is("this is a mapper!"));
    }

    private Mapper<FakeEntity> entityMapper() {
        return (Mapper<FakeEntity>) context.mapperFor(FakeEntity.class);
    }

}
