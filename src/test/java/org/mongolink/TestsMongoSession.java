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

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InOrder;
import org.mongolink.domain.UpdateStrategies;
import org.mongolink.domain.criteria.Criteria;
import org.mongolink.domain.criteria.CriteriaFactory;
import org.mongolink.domain.mapper.ContextBuilder;
import org.mongolink.test.entity.Comment;
import org.mongolink.test.entity.FakeEntity;
import org.mongolink.test.entity.FakeEntityWithNaturalId;
import org.mongolink.test.entity.OtherEntityWithNaturalId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.FakeDB;
import com.mongodb.FakeDBCollection;

public class TestsMongoSession {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Before
	public void before() {
		db = spy(new FakeDB());
		entities = new FakeDBCollection(db, "entity");
		fakeEntities = new FakeDBCollection(db, "fakeentitywithnaturalid");
		db.collections.put("fakeentity", entities);
		db.collections.put("fakeentitywithnaturalid", fakeEntities);
		db.collections.put("otherentitywithnaturalid", new FakeDBCollection(db, "otherentitywithnaturalid"));
		ContextBuilder cb = new ContextBuilder("org.mongolink.test.simpleMapping");
		session = new MongoSession(db, new CriteriaFactory());
		session.setMappingContext(cb.createContext());
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
	public void canGetByAutoId() {
		createEntity("4d53b7118653a70549fe1b78", "plop");
		createEntity("4d53b7118653a70549fe1b78", "plap");

		FakeEntity entity = session.get("4d53b7118653a70549fe1b78", FakeEntity.class);

		assertThat(entity, notNullValue());
		assertThat(entity.getValue(), is("plop"));
	}

	@Test
	public void cantGetSomethingWichIsNotAnEntity() {
		exception.expect(MongoLinkError.class);
		exception.expectMessage("Comment is not an entity");
		session.get("pouet", Comment.class);
	}

	@Test
	public void canGetByNaturalId() {
		DBObject dbo = new BasicDBObject();
		dbo.put("_id", "a natural key");
		fakeEntities.insert(dbo);

		FakeEntityWithNaturalId entity = session.get("a natural key", FakeEntityWithNaturalId.class);

		assertThat(entity, notNullValue());
		assertThat(entity.getNaturalKey(), is("a natural key"));
	}

	@Test
	public void canSave() {
		FakeEntity entity = new FakeEntity("value");

		session.save(entity);

		assertThat(entities.getObjects().size(), is(1));
		DBObject dbo = entities.getObjects().get(0);
		assertThat(dbo.get("value"), is((Object) "value"));
	}

	@Test
	public void cantSaveSomethingWichIsNotAnEntity() {
		exception.expect(MongoLinkError.class);
		exception.expectMessage("Comment is not an entity");
		session.save(new Comment());
	}

	@Test
	public void canUpdate() {
		createEntity("4d53b7118653a70549fe1b78", "url de test");
		FakeEntity entity = session.get("4d53b7118653a70549fe1b78", FakeEntity.class);
		entity.setValue("un test de plus");

		session.update(entity);

		assertThat(entities.getObjects().get(0).get("value"), is((Object) "un test de plus"));
	}

	@Test
	public void cantUpdateSomethingWichIsNotAnEntity() {
		exception.expect(MongoLinkError.class);
		exception.expectMessage("Comment is not an entity");
		session.update(new Comment());
	}

	@Test
	public void canAutomaticalyUpdate() {
		createEntity("4d53b7118653a70549fe1b78", "url de test");
		session.start();
		FakeEntity fakeEntity = session.get("4d53b7118653a70549fe1b78", FakeEntity.class);
		fakeEntity.setValue("some new and strange value");

		session.stop();

		DBObject dbObject = entities.getObjects().get(0);
		assertThat(dbObject.get("value"), is(((Object) "some new and strange value")));
	}

	@Test
	public void returnNullIfNotFound() {
		FakeEntityWithNaturalId entity = session.get("a natural key", FakeEntityWithNaturalId.class);

		assertThat(entity, nullValue());
	}

	@Test
	public void canUpdateJustSavedEntityWithNaturalId() {
		FakeEntityWithNaturalId entity = new FakeEntityWithNaturalId("natural key");
		session.save(entity);
		entity.setValue("a value");

		session.stop();

		assertThat(fakeEntities.getObjects().get(0).get("value"), is((Object) "a value"));
	}

	@Test
	public void canUpdateJustSavedEntityWithAutoId() {
		FakeEntity entity = new FakeEntity("this is a value");
		session.save(entity);
		entity.setValue("a value");

		session.stop();

		assertThat(entities.getObjects().get(0).get("value"), is((Object) "a value"));
	}

	@Test
	public void canUpdateWithDiffStategy() {
		session.setUpdateStrategy(UpdateStrategies.DIFF);
		FakeEntity entity = new FakeEntity("this is a value");
		session.save(entity);
		entity.setValue("a value");

		session.update(entity);

		final DBObject update = entities.lastUpdate();
		assertThat(update.containsField("$set"), is(true));
	}

	@Test
	public void dontMakeUpdateTwice() {
		session.setUpdateStrategy(UpdateStrategies.DIFF);
		FakeEntity entity = new FakeEntity("this is a value");
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
		FakeEntity entity = new FakeEntity("this is a value");
		session.save(entity);

		session.update(entity);

		final DBObject update = entities.lastUpdate();
		assertThat(update, nullValue());
	}

	@Test
	public void savingSetIdForAutoId() {
		FakeEntity entity = new FakeEntity("a value");

		session.save(entity);

		assertThat(entity.getId(), notNullValue());
	}

	@Test
	public void canGetCriteria() {
		final Criteria<FakeEntity> criteria = session.createCriteria(FakeEntity.class);

		assertThat(criteria, notNullValue());
	}

	@Test
	public void canGetByCriteria() {
		session.save(new FakeEntity("this is a value"));
		session.save(new FakeEntity("this is a value"));
		final Criteria<FakeEntity> criteria = session.createCriteria(FakeEntity.class);

		List<FakeEntity> list = criteria.list();

		assertThat(list.size(), is(2));
	}

	@Test
	public void returnSameInstanceOnGetById() {
		DBObject dbo = new BasicDBObject();
		dbo.put("_id", "a natural key");
		fakeEntities.insert(dbo);

		FakeEntityWithNaturalId first = session.get("a natural key", FakeEntityWithNaturalId.class);
		FakeEntityWithNaturalId second = session.get("a natural key", FakeEntityWithNaturalId.class);

		assertThat(first, sameInstance(second));
	}

	@Test
	public void returnSameInstanceOnGetByCriteria() {
		createEntity("4d53b7118653a70549fe1b78", "plop");
		final Criteria criteria = session.createCriteria(FakeEntity.class);

		final FakeEntity instanceById = session.get("4d53b7118653a70549fe1b78", FakeEntity.class);
		final FakeEntity instanceByCriteria = (FakeEntity) criteria.list().get(0);

		assertThat(instanceById, sameInstance(instanceByCriteria));
	}

	@Test
	public void entitiesAreCachedUsingTheirTypeAndId() {
		session.save(new FakeEntityWithNaturalId("cle unique"));
		session.save(new OtherEntityWithNaturalId("cle unique"));

		final FakeEntityWithNaturalId entity = session.get("cle unique", FakeEntityWithNaturalId.class);

		assertThat(entity, notNullValue());
	}

	@Test
	public void canClear() {
		final FakeEntityWithNaturalId element = new FakeEntityWithNaturalId("cle unique");
		session.save(element);

		session.clear();

		final FakeEntityWithNaturalId elementFound = session.get("cle unique", FakeEntityWithNaturalId.class);
		assertThat(elementFound, not(element));
	}

	@Test
	public void canGetAll() {
		createEntity("4d53b7118653a70549fe1b78", "plop");
		createEntity("4d53b7118653a70549fe1b78", "plap");

		List<FakeEntity> entityList = session.getAll(FakeEntity.class);

		assertThat(entityList, notNullValue());
		assertThat(entityList.size(), is(2));
	}

	@Test
	public void canExecutePageQuery() {

	}

	@Test
	public void canDeleteEntity() {
		final FakeEntityWithNaturalId entity = new FakeEntityWithNaturalId("cle unique");
		session.save(entity);

		session.delete(entity);

		assertThat(entities.getObjects().size(), is(0));
		assertThat(session.get("cle unique", FakeEntityWithNaturalId.class), nullValue());
	}

	@Test
	public void cantDeleteEntityNotInCache() {
		final FakeEntityWithNaturalId entity = new FakeEntityWithNaturalId("cle unique");
		session.save(entity);
		session.clear();

		exception.expect(MongoLinkError.class);
		exception.expectMessage("Entity to delete not loaded");
		session.delete(entity);
	}

	@Test
	public void cantDeleteSomethingWichIsNotAnEntity() {
		exception.expect(MongoLinkError.class);
		exception.expectMessage("Comment is not an entity");
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
	private FakeDBCollection fakeEntities;

}
