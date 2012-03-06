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

import org.mongolink.domain.criteria.CriteriaFactory;
import org.mongolink.domain.mapper.ClassMapper;
import org.mongolink.domain.mapper.ContextBuilder;
import org.mongolink.domain.mapper.EntityMapper;
import org.mongolink.domain.mapper.MapperContext;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;

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
                EntityMapper<?> entityMapper = (EntityMapper<?>) mapper;
                final MongoSession session = createSession();
                if (!session.getDb().collectionExists(entityMapper.collectionName())) {
                    session.start();
                    final DBObject options = new BasicDBObject();
                    options.put("capped", "true");
                    options.put("size", entityMapper.getCappedSize());
                    options.put("max", entityMapper.getCappedMax());
                    session.getDb().createCollection(entityMapper.collectionName(), options);
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
