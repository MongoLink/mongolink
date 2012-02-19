package fr.bodysplash.mongolink.domain;

import java.util.Map;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import com.mongodb.DBObject;

import fr.bodysplash.mongolink.MongoSession;

public class UnitOfWork {

	public UnitOfWork(MongoSession session) {
		this.session = session;
	}

	public void add(Object id, Object entity, DBObject initialValue) {
		values.put(new Key(entity.getClass(), id), new Value(entity, initialValue));
	}

	public void flush() {
		for (Value value : values.values()) {
			session.update(value.entity);
		}
	}

	public boolean contains(Class<?> type, Object dbId) {
		return values.containsKey(new Key(type, dbId));
	}

	@SuppressWarnings("unchecked")
	public <T> T getEntity(Class<?> type, Object dbId) {
		return (T) getValue(type, dbId).entity;
	}

	public DBObject getDBOBject(Class<?> type, Object dbId) {
		return getValue(type, dbId).initialValue;
	}

	private Value getValue(Class<?> type, Object dbId) {
		return values.get(new Key(type, dbId));
	}

	public void update(Object id, Object element, DBObject update) {
		values.put(new Key(element.getClass(), id), new Value(element, update));
	}

	public void clear() {
		values.clear();
	}

	public void delete(Object id, Object element) {
		values.remove(new Key(element.getClass(), id));
	}

	private class Value {

		private Value(Object entity, DBObject initialValue) {
			this.entity = entity;
			this.initialValue = initialValue;
		}

		Object entity;
		DBObject initialValue;
	}

	private class Key {

		private Key(Class<?> type, Object id) {
			this.type = type;
			this.id = id;
		}

		@Override
		public boolean equals(Object o) {
			Key other = (Key) o;
			return Objects.equal(type, other.type) && Objects.equal(id, other.id);
		}

		@Override
		public int hashCode() {
			return Objects.hashCode(type, id);
		}

		Class<?> type;

		Object id;
	}

	private MongoSession session;

	private Map<Key, Value> values = Maps.newHashMap();

}
