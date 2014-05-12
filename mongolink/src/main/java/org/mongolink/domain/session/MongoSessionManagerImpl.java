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

import com.mongodb.DB;
import com.mongodb.DBObject;
import org.mongolink.*;
import org.mongolink.domain.criteria.CriteriaFactory;
import org.mongolink.domain.mapper.*;

public class MongoSessionManagerImpl implements MongoSessionManager {

    public MongoSessionManagerImpl(MapperContext mapperContext, DbFactory dbFactory, Settings settings) {
        this.mapperContext = mapperContext;
        this.dbFactory = dbFactory;
        this.settings = settings;
    }

    public void createCappedCollections() {
        for (ClassMapper mapper : mapperContext.getMappers()) {
            if (mapper.isCapped()) {
                AggregateMapper<?> aggregateMapper = (AggregateMapper<?>) mapper;
                final MongoSessionImpl session = createSession();
                if (!session.getDb().collectionExists(aggregateMapper.collectionName())) {
                    session.start();
                    final DBObject options = aggregateMapper.getCapped().getDbValue();
                    session.getDb().createCollection(aggregateMapper.collectionName(), options);
                    session.stop();
                }
            }
        }
    }

    @Override
    public MongoSessionImpl createSession() {
        MongoSessionImpl mongoSession = new MongoSessionImpl(getDb(), getCriteriaFactory());
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

    @Override
    public void close() {
        dbFactory.close();
    }

    private final MapperContext mapperContext;
    private volatile DbFactory dbFactory;
    private volatile Settings settings;
}
