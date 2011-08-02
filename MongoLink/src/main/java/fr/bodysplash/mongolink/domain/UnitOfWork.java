package fr.bodysplash.mongolink.domain;

import com.google.common.collect.Maps;
import com.mongodb.DBObject;
import fr.bodysplash.mongolink.MongoSession;

import java.util.Map;

public class UnitOfWork {

    public UnitOfWork(MongoSession session) {
        this.session = session;
    }

    public void add(Object id, Object entity, DBObject initialValue) {
        values.put(id, new Value(entity, initialValue));
    }

    public void flush() {
        for (Value value : values.values()) {
            session.update(value.entity);
        }
    }

    public boolean contains(Object dbId) {
        return values.containsKey(dbId);
    }

    public <T> T getEntity(Object dbId) {
        return (T) values.get(dbId).entity;
    }

    public DBObject getDBOBject(Object dbId) {
        return values.get(dbId).initialValue;
    }

    public void update(Object id, Object element, DBObject update) {
        values.put(id, new Value(element, update));
    }

    private class Value {

        private Value(Object entity, DBObject initialValue) {
            this.entity = entity;
            this.initialValue = initialValue;
        }

        Object entity;
        DBObject initialValue;
    }

    private MongoSession session;
    private Map<Object, Value> values = Maps.newHashMap();
}
