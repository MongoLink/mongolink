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

package org.mongolink.domain.session;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.mongolink.MongoLinkError;
import org.mongolink.MongoSession;
import org.mongolink.domain.mapper.AggregateMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.stream.Collectors;


public class UnitOfWork {

    public UnitOfWork(MongoSessionImpl session) {
        this.session = session;
    }

    public void registerDirty(Object id, Object entity, DBObject initialValue) {
        register(id, entity, initialValue, ValueState.DIRTY);
    }

    public void registerNew(Object entity) {
        AggregateMapper<?> mapper = session.entityMapper(entity.getClass());
        DBObject initialValue = mapper.toDBObject(entity);
        mapper.populateId(entity, initialValue);
        register(mapper.getId(initialValue), entity, initialValue, ValueState.NEW);
    }

    public void registerDelete(Object element) {
        AggregateMapper<?> mapper = session.entityMapper(element.getClass());
        final Object id = mapper.getId(element);
        checkEntityIsInCache(element, id);
        getValue(element.getClass(), id).markForDeletion();
    }

    private void checkEntityIsInCache(Object element, Object id) {
        if (!contains(element.getClass(), id)) {
            throw new MongoLinkError("Entity to delete not loaded");
        }
    }

    public void commit() {
        LOGGER.debug("Commiting unit of work");
        values.values().forEach(v->v.commit(session));
        clearDeletedAggregates();
        LOGGER.debug("Done commiting");
    }

    private void clearDeletedAggregates() {
        values.entrySet().stream()
                .filter(entry -> entry.getValue().isDeleted())
                .map(e -> e.getKey())
                .collect(Collectors.toSet())
                .forEach(values::remove);
    }

    @SuppressWarnings("unchecked")
    public <T> T getEntity(Class<?> type, Object dbId) {
        return (T) getValue(type, dbId).entity;
    }

    public boolean contains(Class<?> type, Object dbId) {
        if (type != null && dbId != null) {
            final Key craftedKey = new Key(type, dbId);
            for (Key key : values.keySet()) {
                if (key.matchs(craftedKey)) {
                    return true;
                }
            }
        }
        return false;
    }

    private Value getValue(Class<?> type, Object dbId) {
        Key craftedKey = new Key(type, dbId);
        for (Key key : values.keySet()) {
            if (key.matchs(craftedKey)) {
                return values.get(key);
            }
        }
        return values.get(craftedKey);
    }

    private void register(Object id, Object entity, DBObject initialValue, ValueState state) {
        values.put(new Key(entity.getClass(), id), new Value(entity, initialValue, state));
    }

    public void clear() {
        values.clear();
    }

    public void delete(Object id, Object element) {
        values.remove(new Key(element.getClass(), id));
    }

    private class Value {

        private Value(Object entity, DBObject initialValue, ValueState state) {
            this.entity = entity;
            this.initialValue = initialValue;
            this.state = state;
        }

        public void commit(MongoSessionImpl session) {
            state.commit(session, this);
        }

        private boolean isDeleted() {
            return state == ValueState.DELETED;
        }

        private void markForDeletion() {
            state = ValueState.DELETED;
        }

        final Object entity;
        DBObject initialValue;
        private ValueState state;
    }

    private enum ValueState {
        NEW {
            @Override
            public void commit(MongoSessionImpl session, Value value) {
                final AggregateMapper<?> mapper = session.entityMapper(value.entity.getClass());
                final DBObject newValue = mapper.toDBObject(value.entity);
                session.getDbCollection(mapper).insert(newValue);
                UnitOfWork.LOGGER.debug("Entity added :{}", newValue);
                value.initialValue = newValue;
                value.state = DIRTY;
            }
        }, DIRTY {
            @Override
            public void commit(MongoSessionImpl session, Value value) {
                AggregateMapper<?> mapper = session.entityMapper(value.entity.getClass());
                DBObject initialValue = value.initialValue;
                DBObject updatedValue = mapper.toDBObject(value.entity);
                session.getUpdateStrategy().update(initialValue, updatedValue, session.getDbCollection(mapper));
                value.initialValue = updatedValue;
            }
        }, DELETED {
            @Override
            public void commit(MongoSessionImpl mongoSession, Value value) {
                final AggregateMapper<?> mapper = mongoSession.entityMapper(value.entity.getClass());
                LOGGER.debug("Deleting entity : {}", value.entity);
                mongoSession.getDbCollection(mapper).remove(new BasicDBObject("_id", mapper.getId(value.initialValue)));
            }
        };

        public void commit(MongoSessionImpl mongoSession, Value value) {

        }
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

        public boolean matchs(Key otherKey) {
            return otherKey.type.isAssignableFrom(type) && Objects.equal(id, otherKey.id);
        }

        final Class<?> type;

        final Object id;
    }

    private final MongoSessionImpl session;

    private final Map<Key, Value> values = Maps.newHashMap();

    private static final Logger LOGGER = LoggerFactory.getLogger(MongoSession.class);

}
