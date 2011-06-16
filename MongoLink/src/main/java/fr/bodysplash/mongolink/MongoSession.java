package fr.bodysplash.mongolink;


import com.google.common.collect.Lists;
import com.mongodb.*;
import fr.bodysplash.mongolink.criteria.Criteria;
import fr.bodysplash.mongolink.mapper.EntityMapper;
import fr.bodysplash.mongolink.mapper.Mapper;
import fr.bodysplash.mongolink.mapper.MapperContext;

import java.util.List;

public class MongoSession {

    public MongoSession(DB db) {
        this.db = db;
    }

    public void start() {
        db.requestStart();
    }

    public void stop() {
        for (Object o : unitOfWork) {
            update(o);
        }
        db.requestDone();
    }

    public void setMappingContext(MapperContext context) {
        this.context = context;
    }

    public <T> T get(String id, Class<T> entityType) {
        EntityMapper<T> mapper = (EntityMapper<T>) entityMapper(entityType);
        Object dbId = mapper.getDbId(id);
        DBObject query = new BasicDBObject("_id", dbId);
        final List<T> list = executeQuery(entityType, query);
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    public <T> List<T> executeQuery(Class<T> type, DBObject query) {
        EntityMapper<T> mapper = (EntityMapper<T>) entityMapper(type);
        final List<T> result = Lists.newArrayList();
        DBCollection collection = db.getCollection(mapper.collectionName());
        DBCursor cursor = collection.find();
        while (cursor.hasNext()) {
            final DBObject dbObject = cursor.next();
            T entity = mapper.toInstance(dbObject);
            unitOfWork.add(entity);
            result.add(entity);
        }
        return result;
    }

    public void save(Object element) {
        EntityMapper<?> mapper = entityMapper(element.getClass());
        DBObject dbObject = mapper.toDBObject(element);
        db.getCollection(mapper.collectionName()).insert(dbObject);
        mapper.populateId(element, dbObject);
        unitOfWork.add(element);
    }

    public void update(Object element) {
        EntityMapper<?> mapper = entityMapper(element.getClass());
        DBCollection collection = db.getCollection(mapper.collectionName());
        DBObject update = mapper.toDBObject(element);
        DBObject query = new BasicDBObject();
        query.put("_id", update.get("_id"));
        collection.update(query, update);
    }

    private EntityMapper<?> entityMapper(Class<?> type) {
        checkIsAnEntity(type);
        return (EntityMapper<?>) context.mapperFor(type);
    }

    private void checkIsAnEntity(Class<?> entityType) {
        Mapper<?> mapper = context.mapperFor(entityType);
        if (mapper == null || !(mapper instanceof EntityMapper)) {
            throw new MongoLinkError(entityType.getName() + " is not an entity");
        }
    }

    public DB getDb() {
        return db;
    }

    public Criteria createCriteria(Class<?> type) {
        return new Criteria(type, this);
    }

    private final DB db;
    private MapperContext context;
    private final List<Object> unitOfWork = Lists.newArrayList();
}
