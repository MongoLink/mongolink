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

package org.mongolink.domain.session;

import com.mongodb.*;
import org.mongolink.*;
import org.mongolink.domain.criteria.Criteria;
import org.mongolink.domain.criteria.CriteriaFactory;
import org.mongolink.domain.mapper.*;
import org.mongolink.domain.query.QueryExecutor;
import org.mongolink.domain.updateStrategy.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.google.common.base.Preconditions.*;

public class MongoSessionImpl implements MongoSession {

    public MongoSessionImpl(DB db, CriteriaFactory criteriaFactory) {
        this.db = db;
        this.criteriaFactory = criteriaFactory;
    }

    @Override
    public void start() {
        state = state.start(db);
    }

    @Override
    public void stop() {
        state = state.stop(db, unitOfWork);
    }

    protected void setMappingContext(MapperContext context) {
        this.context = context;
    }

    @Override
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

    AggregateMapper<?> entityMapper(Class<?> type) {
        checkIsAnEntity(type);
        return (AggregateMapper<?>) context.mapperFor(type);
    }

    private void checkIsAnEntity(Class<?> entityType) {
        ClassMapper<?> mapper = context.mapperFor(entityType);
        if (mapper == null || !(mapper instanceof AggregateMapper)) {
            throw new MongoLinkError(entityType.getName() + " is not an entity");
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> getAll(Class<T> entityType) {
        checkNotNull(entityType, "Entity type was null");
        state.ensureStarted();
        return createCriteria(entityType).list();
    }

    @Override
    public void save(Object element) {
        checkNotNull(element, "Element to save was null");
        state.ensureStarted();
        unitOfWork.registerNew(element);
        LOGGER.debug("New entity register for additon : {}",  element);
    }

    DBCollection getDbCollection(AggregateMapper<?> mapper) {
        return db.getCollection(mapper.collectionName());
    }

    @Override
    public void delete(Object element) {
        checkNotNull(element, "Element was null");
        state.ensureStarted();
        unitOfWork.registerDelete(element);
        LOGGER.debug("Entity register for deletion : " + element);
    }

    @Override
    public void clear() {
        unitOfWork.clear();
    }

    @Override
    public DB getDb() {
        return db;
    }

    @Override
    public <U> Criteria createCriteria(Class<U> type) {
        checkNotNull(type, "Type was null");
        state.ensureStarted();
        AggregateMapper<?> mapper = entityMapper(type);
        Criteria criteria = criteriaFactory.create(createExecutor(mapper));
        mapper.applyRestrictionsFor(type, criteria);
        return criteria;
    }

    @SuppressWarnings("unchecked")
    private QueryExecutor createExecutor(AggregateMapper<?> mapper) {
        return new QueryExecutor(db, mapper, unitOfWork);
    }

    UpdateStrategy getUpdateStrategy() {
        return updateStrategy;
    }

    public void setUpdateStrategy(UpdateStrategies updateStrategy) {
        checkNotNull(updateStrategy, "Update strategy was null");
        switch (updateStrategy) {
            case DIFF:
                this.updateStrategy = new DiffStrategy();
                break;
            case OVERWRITE:
                this.updateStrategy = new OverwriteStrategy();
                break;
        }
    }

    @Override
    public void flush() {
        state.ensureStarted();
        unitOfWork.commit();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(MongoSession.class);
    private final DB db;
    private final UnitOfWork unitOfWork = new UnitOfWork(this);
    private final CriteriaFactory criteriaFactory;
    public SessionState state = SessionState.NOTSTARTED;
    private MapperContext context;
    private UpdateStrategy updateStrategy = new OverwriteStrategy();
}
