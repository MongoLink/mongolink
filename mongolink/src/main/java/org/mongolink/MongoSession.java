/*
 * MongoLink, Object Document Mapper for Java and MongoDB
 *
 * Copyright (c) 2012, Arpinum or third-party contributors as
 * indicated by the contributors.txt file
 *
 * MongoLink is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * MongoLink is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the Lesser GNU General Public License
 * along with MongoLink.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.mongolink;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import org.mongolink.domain.QueryExecutor;
import org.mongolink.domain.UnitOfWork;
import org.mongolink.domain.UpdateStrategies;
import org.mongolink.domain.criteria.Criteria;
import org.mongolink.domain.criteria.CriteriaFactory;
import org.mongolink.domain.mapper.AggregateMapper;
import org.mongolink.domain.mapper.ClassMapper;
import org.mongolink.domain.mapper.MapperContext;
import org.mongolink.domain.updateStrategy.OverwriteStrategy;
import org.mongolink.domain.updateStrategy.UpdateStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class MongoSession {

    public MongoSession(DB db, CriteriaFactory criteriaFactory) {
        this.db = db;
        this.criteriaFactory = criteriaFactory;
    }

    public void start() {
        state = state.start(db);
    }

    public void stop() {
        state = state.stop(db, unitOfWork);
    }

    protected void setMappingContext(MapperContext context) {
        this.context = context;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Object id, Class<T> entityType) {
        checkNotNull(id, "Id was null");
        checkNotNull(entityType, "Entity type was null");
        state.ensureStarted();
        if (unitOfWork.contains(entityType, id)) {
            return unitOfWork.getEntity(entityType, id);
        }
        AggregateMapper<T> mapper = (AggregateMapper<T>) entityMapper(entityType);
        Object dbId = mapper.getDbId(id);
        DBObject query = new BasicDBObject("_id", dbId);
        return (T) createExecutor(mapper).executeUnique(query);
    }

    private AggregateMapper<?> entityMapper(Class<?> type) {
        checkIsAnEntity(type);
        return (AggregateMapper<?>) context.mapperFor(type);
    }

    private void checkIsAnEntity(Class<?> entityType) {
        ClassMapper<?> mapper = context.mapperFor(entityType);
        if (mapper == null || !(mapper instanceof AggregateMapper)) {
            throw new MongoLinkError(entityType.getName() + " is not an entity");
        }
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getAll(Class<T> entityType) {
        checkNotNull(entityType, "Entity type was null");
        state.ensureStarted();
        return createExecutor(entityMapper(entityType)).execute(new BasicDBObject());
    }

    public void save(Object element) {
        checkNotNull(element, "Element to save was null");
        state.ensureStarted();
        AggregateMapper<?> mapper = entityMapper(element.getClass());
        DBObject dbObject = mapper.toDBObject(element);
        getDbCollection(mapper).insert(dbObject);
        mapper.populateId(element, dbObject);
        unitOfWork.add(mapper.getId(element), element, dbObject);
        LOGGER.debug("New entity created : {}",  dbObject);
    }

    public void update(Object element) {
        checkNotNull(element, "Element was null");
        state.ensureStarted();
        AggregateMapper<?> mapper = entityMapper(element.getClass());
        DBObject initialValue = unitOfWork.getDBOBject(element.getClass(), mapper.getId(element));
        DBObject updatedValue = mapper.toDBObject(element);
        updateStrategy.update(initialValue, updatedValue, getDbCollection(mapper));
        unitOfWork.update(mapper.getId(element), element, updatedValue);
    }

    private DBCollection getDbCollection(AggregateMapper<?> mapper) {
        return db.getCollection(mapper.collectionName());
    }

    public void delete(Object element) {
        checkNotNull(element, "Element was null");
        state.ensureStarted();
        AggregateMapper<?> mapper = entityMapper(element.getClass());
        checkEntityIsInCache(element, mapper);
        DBObject value = unitOfWork.getDBOBject(element.getClass(), mapper.getId(element));
        getDbCollection(mapper).remove(value);
        unitOfWork.delete(mapper.getId(element), element);
        LOGGER.debug("Entity deleted : " + value);
    }

    private void checkEntityIsInCache(Object element, AggregateMapper<?> mapper) {
        if (!unitOfWork.contains(element.getClass(), mapper.getId(element))) {
            throw new MongoLinkError("Entity to delete not loaded");
        }
    }

    public DB getDb() {
        return db;
    }

    public <U> Criteria createCriteria(Class<U> type) {
        checkNotNull(type, "Type was null");
        return criteriaFactory.create(createExecutor(entityMapper(type)));
    }

    @SuppressWarnings("unchecked")
    private QueryExecutor createExecutor(AggregateMapper<?> mapper) {
        return new QueryExecutor(db, mapper, unitOfWork);
    }

    public UpdateStrategy getUpdateStrategy() {
        return updateStrategy;
    }

    public void setUpdateStrategy(UpdateStrategies updateStrategy) {
        checkNotNull(updateStrategy, "Update strategy was null");
        this.updateStrategy = updateStrategy.instance();
    }

    public void clear() {
        unitOfWork.clear();
    }

    public void flush() {
        unitOfWork.flush();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(MongoSession.class);
    private final DB db;
    private final UnitOfWork unitOfWork = new UnitOfWork(this);
    private final CriteriaFactory criteriaFactory;
    public SessionState state = SessionState.NOTSTARTED;
    private MapperContext context;
    private UpdateStrategy updateStrategy = new OverwriteStrategy();
}
