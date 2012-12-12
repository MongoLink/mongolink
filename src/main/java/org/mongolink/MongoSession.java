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
import org.mongolink.domain.*;
import org.mongolink.domain.criteria.*;
import org.mongolink.domain.mapper.*;
import org.mongolink.domain.updateStrategy.*;

import java.util.List;

public class MongoSession {

    public MongoSession(DB db, CriteriaFactory criteriaFactory) {
        this.db = db;
        this.criteriaFactory = criteriaFactory;
    }

    public void start() {
        db.requestStart();
    }

    public void stop() {
        unitOfWork.flush();
        db.requestDone();
    }

    protected void setMappingContext(MapperContext context) {
        this.context = context;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Object id, Class<T> entityType) {
        if (unitOfWork.contains(entityType, id)) {
            return unitOfWork.getEntity(entityType, id);
        }
        AggregateMapper<T> mapper = (AggregateMapper<T>) entityMapper(entityType);
        Object dbId = mapper.getDbId(id);
        DBObject query = new BasicDBObject("_id", dbId);
        return (T) createExecutor(mapper).executeUnique(query);
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getAll(Class<T> entityType) {
        return createExecutor(entityMapper(entityType)).execute(new BasicDBObject());
    }

    public void save(Object element) {
        AggregateMapper<?> mapper = entityMapper(element.getClass());
        DBObject dbObject = mapper.toDBObject(element);
        getDbCollection(mapper).insert(dbObject);
        mapper.populateId(element, dbObject);
        unitOfWork.add(mapper.getId(element), element, dbObject);
    }

    public void update(Object element) {
        AggregateMapper<?> mapper = entityMapper(element.getClass());
        DBObject initialValue = unitOfWork.getDBOBject(element.getClass(), mapper.getId(element));
        DBObject updatedValue = mapper.toDBObject(element);
        updateStrategy.update(initialValue, updatedValue, getDbCollection(mapper));
        unitOfWork.update(mapper.getId(element), element, updatedValue);
    }

    public void delete(Object element) {
        AggregateMapper<?> mapper = entityMapper(element.getClass());
        checkEntityIsInCache(element, mapper);
        DBObject value = unitOfWork.getDBOBject(element.getClass(), mapper.getId(element));
        getDbCollection(mapper).remove(value);
        unitOfWork.delete(mapper.getId(element), element);
    }

    private void checkEntityIsInCache(Object element, AggregateMapper<?> mapper) {
        if (!unitOfWork.contains(element.getClass(), mapper.getId(element))) {
            throw new MongoLinkError("Entity to delete not loaded");
        }
    }

    private DBCollection getDbCollection(AggregateMapper<?> mapper) {
        return db.getCollection(mapper.collectionName());
    }

    public DB getDb() {
        return db;
    }

    public <U> Criteria createCriteria(Class<U> type) {
        return criteriaFactory.create(createExecutor(entityMapper(type)));
    }

    public UpdateStrategy getUpdateStrategy() {
        return updateStrategy;
    }

    public void setUpdateStrategy(UpdateStrategies updateStrategy) {
        this.updateStrategy = updateStrategy.instance();
    }

    public void clear() {
        unitOfWork.clear();
    }

    @SuppressWarnings("unchecked")
    private QueryExecutor createExecutor(AggregateMapper<?> mapper) {
        return new QueryExecutor(db, mapper, unitOfWork);
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

    private final DB db;
    private MapperContext context;
    private final UnitOfWork unitOfWork = new UnitOfWork(this);
    private final CriteriaFactory criteriaFactory;
    private UpdateStrategy updateStrategy = new OverwriteStrategy();
}
