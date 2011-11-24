package fr.bodysplash.mongolink.domain;

import com.google.common.collect.Lists;
import com.mongodb.*;
import fr.bodysplash.mongolink.domain.mapper.*;

import java.util.List;

public class QueryExecutor<T> {

    public QueryExecutor(DB db, EntityMapper<T> mapper, UnitOfWork unitOfWork) {
        this.db = db;
        this.mapper = mapper;
        this.unitOfWork = unitOfWork;
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

    public List<T> execute(DBObject query) {
        return execute(query, CursorParameter.empty());
    }

    public T executeUnique(DBObject query) {
        final List<T> list = execute(query);
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    private T loadEntity(DBObject dbObject) {
        if (unitOfWork.contains(mapper.getPersistentType(), mapper.getId(dbObject))) {
            return unitOfWork.getEntity(mapper.getPersistentType(), mapper.getId(dbObject));
        } else {
            T entity = mapper.toInstance(dbObject);
            unitOfWork.add(mapper.getId(entity), entity, dbObject);
            return entity;
        }
    }

    public DB getDb() {
        return db;
    }

    public EntityMapper<?> getEntityMapper() {
        return mapper;
    }

    private DB db;
    private EntityMapper<T> mapper;
    private MapperContext context;
    private UnitOfWork unitOfWork;
}
