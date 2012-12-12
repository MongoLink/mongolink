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
import org.mongolink.domain.criteria.CriteriaFactory;
import org.mongolink.domain.mapper.*;

public class MongoSessionManager {

    public static MongoSessionManager create(ContextBuilder contextBuilder, Settings settings) {
        MongoSessionManager manager = new MongoSessionManager(contextBuilder.createContext());
        manager.settings = settings;
        manager.dbFactory = settings.createDbFactory();
        manager.createCappedCollections();
        return manager;
    }

    private MongoSessionManager(MapperContext mapperContext) {
        this.mapperContext = mapperContext;
    }

    private void createCappedCollections() {
        for (ClassMapper mapper : mapperContext.getMappers()) {
            if (mapper.isCapped()) {
                AggregateMapper<?> aggregateMapper = (AggregateMapper<?>) mapper;
                final MongoSession session = createSession();
                if (!session.getDb().collectionExists(aggregateMapper.collectionName())) {
                    session.start();
                    final DBObject options = aggregateMapper.getCapped().getDbValue();
                    session.getDb().createCollection(aggregateMapper.collectionName(), options);
                    session.stop();
                }
            }
        }
    }

    public MongoSession createSession() {
        authenticateIfNeeded();
        MongoSession mongoSession = new MongoSession(getDb(), getCriteriaFactory());
        mongoSession.setMappingContext(mapperContext);
        mongoSession.setUpdateStrategy(settings.getUpdateStrategy());
        return mongoSession;
    }

    private void authenticateIfNeeded() {
        if (settings.withAuthentication() && !getDb().isAuthenticated()) {
            getDb().authenticate(settings.getUser(), settings.getPassword().toCharArray());
        }
    }

    private CriteriaFactory getCriteriaFactory() {
        return settings.getCriteriaFactory();
    }

    private DB getDb() {
        return dbFactory.get(settings.getDbName());
    }

    public MapperContext getMapperContext() {
        return mapperContext;
    }

    public void close() {
        dbFactory.close();
    }

    private final MapperContext mapperContext;
    private DbFactory dbFactory;
    private Settings settings;
}
