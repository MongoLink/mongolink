package fr.bodysplash.mongolink.domain;

import com.google.common.collect.Lists;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import fr.bodysplash.mongolink.domain.mapper.EntityMapper;
import fr.bodysplash.mongolink.domain.mapper.MapperContext;

import java.util.List;

public class QueryExecutor {

    public QueryExecutor(DB db, EntityMapper<?> mapper, UnitOfWork unitOfWork) {
        this.db = db;
        this.mapper = mapper;
        this.unitOfWork = unitOfWork;
    }

    public <T> List<T> execute(DBObject query) {
        final List<T> result = Lists.newArrayList();
        DBCollection collection = db.getCollection(mapper.collectionName());
        DBCursor cursor = collection.find(query);
        try {
            while (cursor.hasNext()) {
                result.add((T) loadEntity(cursor.next()));
            }
            return result;
        } finally {
            cursor.close();
        }
    }

    public <T> T executeUnique(DBObject query) {
        final List<T> list = execute(query);
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    private <T> T loadEntity(DBObject dbObject) {
        if (unitOfWork.contains(mapper.getPersistentType(), mapper.getId(dbObject))) {
            return unitOfWork.getEntity(mapper.getPersistentType(), mapper.getId(dbObject));
        } else {
            T entity = (T) mapper.toInstance(dbObject);
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
    private EntityMapper<?> mapper;
    private MapperContext context;
    private UnitOfWork unitOfWork;
}
