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

import com.mongodb.DB;
import com.mongodb.DBObject;
import org.mongolink.domain.criteria.CriteriaFactory;
import org.mongolink.domain.mapper.AggregateMapper;
import org.mongolink.domain.mapper.ClassMapper;
import org.mongolink.domain.mapper.ContextBuilder;
import org.mongolink.domain.mapper.MapperContext;

import static com.google.common.base.Preconditions.checkNotNull;

public class MongoSessionManager {

    private MongoSessionManager(MapperContext mapperContext) {
        this.mapperContext = mapperContext;
    }

    public static MongoSessionManager create(ContextBuilder contextBuilder, Settings settings) {
        checkNotNull(contextBuilder, "Context builder was null");
        checkNotNull(settings, "Settings was null");
        MongoSessionManager manager = new MongoSessionManager(contextBuilder.createContext());
        manager.settings = settings;
        manager.dbFactory = settings.createDbFactory();
        manager.createCappedCollections();
        return manager;
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
        MongoSession mongoSession = new MongoSession(getDb(), getCriteriaFactory());
        mongoSession.setMappingContext(mapperContext);
        mongoSession.setUpdateStrategy(settings.getUpdateStrategy());
        return mongoSession;
    }

    private DB getDb() {
        return dbFactory.get(settings.getDbName());
    }

    private CriteriaFactory getCriteriaFactory() {
        return settings.getCriteriaFactory();
    }


    public MapperContext getMapperContext() {
        return mapperContext;
    }

    public void close() {
        dbFactory.close();
    }

    private final MapperContext mapperContext;
    private volatile DbFactory dbFactory;
    private volatile Settings settings;
}
