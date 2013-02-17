/*
 * MongoLink, Object Document Mapper for Java and MongoDB
 *
 * Copyright (c) 2012, Arpinum or third-party contributors as
 * indicated by the @author tags
 *
 * MongoLink is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MongoLink is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the Lesser GNU General Public License
 * along with MongoLink.  If not, see <http://www.gnu.org/licenses/>. 
 *
 */

package org.mongolink;

import com.mongodb.*;
import org.bson.types.ObjectId;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.mockito.InOrder;
import org.mongolink.domain.UpdateStrategies;
import org.mongolink.domain.criteria.*;
import org.mongolink.domain.mapper.ContextBuilder;
import org.mongolink.test.entity.*;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class TestsMongoSession {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void before() {
        db = spy(new FakeDB());
        entities = new FakeDBCollection(db, "entity");
        fakeAggregates = new FakeDBCollection(db, "fakeaggregatewithnaturalid");
        db.collections.put("fakeaggregate", entities);
        db.collections.put("fakeaggregatewithnaturalid", fakeAggregates);
        db.collections.put("otheraggregatewithnaturalid", new FakeDBCollection(db, "otheraggregatewithnaturalid"));
        ContextBuilder cb = new ContextBuilder("org.mongolink.test.simpleMapping");
        session = new MongoSession(db, new CriteriaFactory());
        session.setMappingContext(cb.createContext());
        session.start();
    }

    @Test
    public void startAndStopASession() {
        MongoSession session = new MongoSession(db, new CriteriaFactory());

        session.start();
        session.stop();

        InOrder inorder = inOrder(db);
        inorder.verify(db).requestStart();
        inorder.verify(db).requestDone();
    }

    @Test
    public void ensureConnection() {
        final FakeDB fakeDb = spy(new FakeDB());
        MongoSession session = new MongoSession(fakeDb, new CriteriaFactory());

        session.start();

        verify(fakeDb).requestEnsureConnection();
    }

    @Test
    public void canGetByAutoId() {
        createEntity("4d53b7118653a70549fe1b78", "plop");
        createEntity("4d53b7118653a70549fe1b78", "plap");

        FakeAggregate entity = session.get("4d53b7118653a70549fe1b78", FakeAggregate.class);

        assertThat(entity, notNullValue());
        assertThat(entity.getValue(), is("plop"));
    }

    @Test
    public void cantGetSomethingWichIsNotAnEntity() {
        exception.expect(MongoLinkError.class);
        session.get("pouet", Comment.class);
    }

    @Test
    public void canGetByNaturalId() {
        DBObject dbo = new BasicDBObject();
        dbo.put("_id", "a natural key");
        fakeAggregates.insert(dbo);

        FakeAggregateWithNaturalId entity = session.get("a natural key", FakeAggregateWithNaturalId.class);

        assertThat(entity, notNullValue());
        assertThat(entity.getNaturalKey(), is("a natural key"));
    }

    @Test
    public void testDavid() {
        DBObject dbo = new BasicDBObject();
        dbo.put("_id", "a natural key");
        fakeAggregates.insert(dbo);

        session.get("a natural key", FakeAggregateWithNaturalId.class);
        FakeAggregateWithNaturalId entity = session.get("a natural key", FakeAggregateWithNaturalId.class);

        entity.setValue("a new hope");
        FakeAggregateWithNaturalId anotherEntity = session.get("a natural key", FakeAggregateWithNaturalId.class);

        assertThat(anotherEntity.getValue(), is("a new hope"));
    }

    @Test
    public void canSave() {
        FakeAggregate entity = new FakeAggregate("value");

        session.save(entity);

        assertThat(entities.getObjects().size(), is(1));
        DBObject dbo = entities.getObjects().get(0);
        assertThat(dbo.get("value"), is((Object) "value"));
    }

    @Test
    public void cantSaveSomethingWichIsNotAnEntity() {
        exception.expect(MongoLinkError.class);
        session.save(new Comment());
    }

    @Test
    public void canUpdate() {
        createEntity("4d53b7118653a70549fe1b78", "url de test");
        FakeAggregate entity = session.get("4d53b7118653a70549fe1b78", FakeAggregate.class);
        entity.setValue("un test de plus");

        session.update(entity);

        assertThat(entities.getObjects().get(0).get("value"), is((Object) "un test de plus"));
    }

    @Test
    public void cantUpdateSomethingWichIsNotAnEntity() {
        exception.expect(MongoLinkError.class);
        session.update(new Comment());
    }

    @Test
    public void canAutomaticalyUpdate() {
        createEntity("4d53b7118653a70549fe1b78", "url de test");
        FakeAggregate fakeAggregate = session.get("4d53b7118653a70549fe1b78", FakeAggregate.class);
        fakeAggregate.setValue("some new and strange value");

        session.stop();

        DBObject dbObject = entities.getObjects().get(0);
        assertThat(dbObject.get("value"), is(((Object) "some new and strange value")));
    }

    @Test
    public void returnNullIfNotFound() {
        FakeAggregateWithNaturalId entity = session.get("a natural key", FakeAggregateWithNaturalId.class);

        assertThat(entity, nullValue());
    }

    @Test
    public void canUpdateJustSavedEntityWithNaturalId() {
        FakeAggregateWithNaturalId entity = new FakeAggregateWithNaturalId("natural key");
        session.save(entity);
        entity.setValue("a value");

        session.stop();

        assertThat(fakeAggregates.getObjects().get(0).get("value"), is((Object) "a value"));
    }

    @Test
    public void canUpdateJustSavedEntityWithAutoId() {
        FakeAggregate entity = new FakeAggregate("this is a value");
        session.save(entity);
        entity.setValue("a value");

        session.stop();

        assertThat(entities.getObjects().get(0).get("value"), is((Object) "a value"));
    }

    @Test
    public void canUpdateWithDiffStategy() {
        session.setUpdateStrategy(UpdateStrategies.DIFF);
        FakeAggregate entity = new FakeAggregate("this is a value");
        session.save(entity);
        entity.setValue("a value");

        session.update(entity);

        final DBObject update = entities.lastUpdate();
        assertThat(update.containsField("$set"), is(true));
    }

    @Test
    public void dontMakeUpdateTwice() {
        session.setUpdateStrategy(UpdateStrategies.DIFF);
        FakeAggregate entity = new FakeAggregate("this is a value");
        session.save(entity);
        entity.setValue("a value");

        session.update(entity);
        final DBObject update = entities.lastUpdate();
        session.update(entity);

        assertThat(update, sameInstance(entities.lastUpdate()));
    }

    @Test
    public void dontMakeUpdateWhenNoDiffWithDiffStrategy() {
        session.setUpdateStrategy(UpdateStrategies.DIFF);
        FakeAggregate entity = new FakeAggregate("this is a value");
        session.save(entity);

        session.update(entity);

        final DBObject update = entities.lastUpdate();
        assertThat(update, nullValue());
    }

    @Test
    public void savingSetIdForAutoId() {
        FakeAggregate entity = new FakeAggregate("a value");

        session.save(entity);

        assertThat(entity.getId(), notNullValue());
    }

    @Test
    public void canGetCriteria() {
        final Criteria<FakeAggregate> criteria = session.createCriteria(FakeAggregate.class);

        assertThat(criteria, notNullValue());
    }

    @Test
    public void canGetByCriteria() {
        session.save(new FakeAggregate("this is a value"));
        session.save(new FakeAggregate("this is a value"));
        final Criteria<FakeAggregate> criteria = session.createCriteria(FakeAggregate.class);

        List<FakeAggregate> list = criteria.list();

        assertThat(list.size(), is(2));
    }

    @Test
    public void returnSameInstanceOnGetById() {
        DBObject dbo = new BasicDBObject();
        dbo.put("_id", "a natural key");
        fakeAggregates.insert(dbo);

        FakeAggregateWithNaturalId first = session.get("a natural key", FakeAggregateWithNaturalId.class);
        FakeAggregateWithNaturalId second = session.get("a natural key", FakeAggregateWithNaturalId.class);

        assertThat(first, sameInstance(second));
    }

    @Test
    public void returnSameInstanceOnGetByCriteria() {
        createEntity("4d53b7118653a70549fe1b78", "plop");
        final Criteria criteria = session.createCriteria(FakeAggregate.class);

        final FakeAggregate instanceById = session.get("4d53b7118653a70549fe1b78", FakeAggregate.class);
        final FakeAggregate instanceByCriteria = (FakeAggregate) criteria.list().get(0);

        assertThat(instanceById, sameInstance(instanceByCriteria));
    }

    @Test
    public void entitiesAreCachedUsingTheirTypeAndId() {
        session.save(new FakeAggregateWithNaturalId("cle unique"));
        session.save(new OtherEntityWithNaturalId("cle unique"));

        final FakeAggregateWithNaturalId entity = session.get("cle unique", FakeAggregateWithNaturalId.class);

        assertThat(entity, notNullValue());
    }

    @Test
    public void canClear() {
        final FakeAggregateWithNaturalId element = new FakeAggregateWithNaturalId("cle unique");
        session.save(element);

        session.clear();

        final FakeAggregateWithNaturalId elementFound = session.get("cle unique", FakeAggregateWithNaturalId.class);
        assertThat(elementFound, not(element));
    }

    @Test
    public void canGetAll() {
        createEntity("4d53b7118653a70549fe1b78", "plop");
        createEntity("4d53b7118653a70549fe1b78", "plap");

        List<FakeAggregate> entityList = session.getAll(FakeAggregate.class);

        assertThat(entityList, notNullValue());
        assertThat(entityList.size(), is(2));
    }

    @Test
    public void canDeleteEntity() {
        final FakeAggregateWithNaturalId entity = new FakeAggregateWithNaturalId("cle unique");
        session.save(entity);

        session.delete(entity);

        assertThat(entities.getObjects().size(), is(0));
        assertThat(session.get("cle unique", FakeAggregateWithNaturalId.class), nullValue());
    }

    @Test
    public void cantDeleteEntityNotInCache() {
        final FakeAggregateWithNaturalId entity = new FakeAggregateWithNaturalId("cle unique");
        session.save(entity);
        session.clear();

        exception.expect(MongoLinkError.class);
        session.delete(entity);
    }

    @Test
    public void cantDeleteSomethingWichIsNotAnEntity() {
        exception.expect(MongoLinkError.class);
        session.delete(new Comment());
    }

    private void createEntity(final String id, final String url) {
        DBObject dbo = new BasicDBObject();
        dbo.put("value", url);
        dbo.put("_id", new ObjectId(id));
        entities.insert(dbo);
    }

    private FakeDBCollection entities;
    private FakeDB db;
    private MongoSession session;
    private FakeDBCollection fakeAggregates;

}
