package fr.bodysplash.mongomapper;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import fr.bodysplash.mongomapper.test.Comment;
import fr.bodysplash.mongomapper.test.Entity;
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
        Mapping<Entity> entityMapping = contextBuilder.newMapping(Entity.class);
        entityMapping.id().getId();
        entityMapping.property().getValue();
        entityMapping.collection().getComments();
        Mapping<Comment> commentMapping = contextBuilder.newMapping(Comment.class);
        commentMapping.property().getValue();
        context = contextBuilder.createContext();
    }

    @Test
    public void canDealWithId() {
        Entity entity = new Entity("test.com");
        entity.setId("id de test");

        DBObject dbObject = entityMapper().toDBObject(entity);

        assertThat(dbObject.get("_id"), is((Object) "id de test"));
    }

    @Test
    public void canSaveProperties() {
        Entity entity = new Entity("test.com");


        DBObject dbo = entityMapper().toDBObject(entity);

        assertThat(dbo, notNullValue());
        assertThat(dbo.get("value"), is((Object) "test.com"));

    }

    @Test
    public void canSaveCollections() {
        Entity entity = new Entity("test.com");
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

        Entity entity = entityMapper().toInstance(dbo);

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

        Entity entity = entityMapper().toInstance(dbo);

        assertThat(entity.getComments().size(), is(1));
        assertThat(entity.getComments().get(0).getValue(), is("this is a mapper!"));
    }

    private Mapper<Entity> entityMapper() {
        return (Mapper<Entity>) context.mapperFor(Entity.class);
    }

}
