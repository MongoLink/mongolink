package fr.bodysplash.mongolink;


import com.mongodb.BasicDBObject;
import com.mongodb.FakeDB;
import com.mongodb.FakeDBCollection;
import fr.bodysplash.mongolink.criteria.CriteriaFactory;
import fr.bodysplash.mongolink.mapper.MapperContext;
import fr.bodysplash.mongolink.test.entity.FakeChildEntity;
import fr.bodysplash.mongolink.test.entity.FakeEntity;
import fr.bodysplash.mongolink.test.inheritanceMapping.FakeEntityWithSubclassMapping;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class TestsMongoSessionWithInheritance {

    @Before
    public void before() {
        db = Mockito.spy(new FakeDB());
        entities = new FakeDBCollection(db, "fakeentity");
        db.collections.put("fakeentity", entities);
        FakeEntityWithSubclassMapping mapping = new FakeEntityWithSubclassMapping();
        session = new MongoSession(db, new CriteriaFactory());
        MapperContext context = new MapperContext();
        mapping.buildMapper(context);
        session.setMappingContext(context);
    }

    @Test
    public void canGetChildEntitiesFromParent() {
        BasicDBObject dbo = new BasicDBObject();
        dbo.put("_id", "1");
        dbo.put("__discriminator", "FakeChildEntity");
        entities.insert(dbo);

        FakeEntity entity = session.get("1", FakeEntity.class);

        assertThat(entity, notNullValue());
        assertThat(entity, instanceOf(FakeChildEntity.class));
    }

    @Test
    public void savesChildEntityInSameCollection() {
        FakeChildEntity fakeChildEntity = new FakeChildEntity();
        fakeChildEntity.setId("2");

        session.save(fakeChildEntity);

        assertThat(entities.count(), is(1L));
    }

    private FakeDBCollection entities;
    private FakeDB db;
    private MongoSession session;
}
