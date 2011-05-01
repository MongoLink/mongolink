package fr.bodysplash.mongolink;


import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
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
        DBCollection collection = db.getCollection(collectionName(entityType));
        Object dbId = mapper.getDbId(id);
        DBObject query = new BasicDBObject("_id", dbId);
        DBObject result = collection.findOne(query);
        if (result == null) {
            return null;
        }
        T entity = mapper.toInstance(result);
        unitOfWork.add(entity);
        return entity;
    }

    public void save(Object element) {
        EntityMapper<?> mapper = entityMapper(element.getClass());
        DBObject dbObject = mapper.toDBObject(element);
        db.getCollection(collectionName(element.getClass())).insert(dbObject);
        mapper.populateId(element, dbObject);
        unitOfWork.add(element);
    }

    public void update(Object element) {
        EntityMapper<?> mapper = entityMapper(element.getClass());
        DBCollection collection = db.getCollection(collectionName(element.getClass()));
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

    private <T> String collectionName(Class<T> clazz) {
        return clazz.getSimpleName().toLowerCase();
    }

    public DB getDb() {
        return db;
    }
    
    private final DB db;
    private MapperContext context;
    private final List<Object> unitOfWork = Lists.newArrayList();
}
