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

package org.mongolink.domain.query;

import com.google.common.collect.Lists;
import com.mongodb.*;
import org.mongolink.domain.mapper.AggregateMapper;
import org.mongolink.domain.session.UnitOfWork;

import java.util.List;

public class QueryExecutor<T> {

    public QueryExecutor(DB db, AggregateMapper<T> mapper, UnitOfWork unitOfWork) {
        this.db = db;
        this.mapper = mapper;
        this.unitOfWork = unitOfWork;
    }

    public List<T> execute(DBObject query) {
        return execute(query, CursorParameter.empty());
    }

    public List<T> execute(DBObject query, CursorParameter cursorParameter) {
        final List<T> result = Lists.newArrayList();
        DBCollection collection = db.getCollection(mapper.collectionName());
        DBCursor cursor = collection.find(query);
        cursor = cursorParameter.apply(cursor);
        try {
            while (cursor.hasNext()) {
                result.add(loadEntity(cursor.next()));
            }
            return result;
        } finally {
            cursor.close();
        }
    }

    public T executeUnique(DBObject query) {
        final List<T> list = execute(query);
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    private T loadEntity(DBObject dbObject) {
        final Object unitOfWorkId = mapper.getId(dbObject);
        if (unitOfWork.contains(mapper.getPersistentType(), unitOfWorkId)) {
            return unitOfWork.getEntity(mapper.getPersistentType(), unitOfWorkId);
        } else {
            T entity = mapper.toInstance(dbObject);
            unitOfWork.registerDirty(unitOfWorkId, entity, dbObject);
            return entity;
        }
    }

    private final DB db;
    private final AggregateMapper<T> mapper;
    private final UnitOfWork unitOfWork;
}
