package fr.bodysplash.mongomapper;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import fr.bodysplash.mongomapper.test.Comment;
import fr.bodysplash.mongomapper.test.FakeEntity;
import fr.bodysplash.mongomapper.test.FakeEntityWithNaturalId;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;

import java.net.UnknownHostException;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;


public class MapperTests {


    private MappingContext context;

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

        assertThat(dbObject.get("_id"), is((Object) new ObjectId("4d53b7118653a70549fe1b78")));
    }

    @Test
    public void canSaveNaturalId() {
        MappingContext mappingContext = contextWithNaturalId();
        FakeEntityWithNaturalId entity = new FakeEntityWithNaturalId("natural key");

        DBObject dbObject = mappingContext.mapperFor(FakeEntityWithNaturalId.class).toDBObject(entity);

        assertThat(dbObject.get("_id"), is((Object) "natural key"));
    }

    @Test
    public void canPopulateNaturalId() {
        MappingContext mappingContext = contextWithNaturalId();
        DBObject dbo = new BasicDBObject();
        dbo.put("_id", "natural key");

        FakeEntityWithNaturalId instance = mappingContext.mapperFor(FakeEntityWithNaturalId.class).toInstance(dbo);

        assertThat(instance.getNaturalKey(), is((Object) "natural key"));
    }


    private MappingContext contextWithNaturalId() {
        ContextBuilder contextBuilder = new ContextBuilder();
        Mapping<FakeEntityWithNaturalId> mapping = contextBuilder.newMapping(FakeEntityWithNaturalId.class);
        mapping.id(IdGeneration.Natural).getNaturalKey();
        return contextBuilder.createContext();
    }

    @Test
    public void canSaveProperties() {
        FakeEntity entity = new FakeEntity("test.com");


        DBObject dbo = entityMapper().toDBObject(entity);

        assertThat(dbo, notNullValue());
        assertThat(dbo.get("value"), is((Object) "test.com"));

    }

    @Test
    public void canSaveCollections() {
        FakeEntity entity = new FakeEntity("test.com");
        entity.addComment("un commentaire");

        DBObject dbo = entityMapper().toDBObject(entity);

        assertThat(dbo, notNullValue());
        assertThat(dbo.get("comments"), notNullValue());
        BasicDBList comments = (BasicDBList) dbo.get("comments");
        assertThat(comments.size(), is(1));
        DBObject comment = (DBObject) comments.get(0);
        assertThat(comment.get("value"), is((Object) "un commentaire"));

    }

    @Test
    public void canPopulateProperties() {
        DBObject dbo = new BasicDBObject();
        dbo.put("value", "test.com");
        dbo.put("_id", "id");

        FakeEntity entity = entityMapper().toInstance(dbo);

        assertThat(entity, notNullValue());
        assertThat(entity.getValue(), is("test.com"));
        assertThat(entity.getId(), is("id"));
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

        assertThat(entity.getComments().size(), is(1));
        assertThat(entity.getComments().get(0).getValue(), is("this is a mapper!"));
    }

    private Mapper<FakeEntity> entityMapper() {
        return (Mapper<FakeEntity>) context.mapperFor(FakeEntity.class);
    }

}
