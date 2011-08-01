package fr.bodysplash.mongolink;


import com.google.common.collect.Lists;
import com.mongodb.*;
import fr.bodysplash.mongolink.domain.UnitOfWork;
import fr.bodysplash.mongolink.domain.criteria.Criteria;
import fr.bodysplash.mongolink.domain.criteria.CriteriaFactory;
import fr.bodysplash.mongolink.domain.mapper.*;
import fr.bodysplash.mongolink.domain.mapper.EntityMapper;
import fr.bodysplash.mongolink.domain.mapper.MapperContext;

import java.util.List;

public class MongoSession {

    public MongoSession(DB db, CriteriaFactory criteriaFactory) {
        this.db = db;
        this.criteriaFactory = criteriaFactory;
    }

    public void start() {
        db.requestStart();
    }

    public void stop() {
        unitOfWork.flush();
        db.requestDone();
    }

    public void setMappingContext(MapperContext context) {
        this.context = context;
    }

    public <T> T get(String id, Class<T> entityType) {
        EntityMapper<T> mapper = (EntityMapper<T>) entityMapper(entityType);
        Object dbId = mapper.getDbId(id);
        if(unitOfWork.contains(dbId)) {
            return unitOfWork.get(dbId);
        }
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
        DBCursor cursor = collection.find(query);
        try {
            while (cursor.hasNext()) {
                final DBObject dbObject = cursor.next();
                T entity = mapper.toInstance(dbObject);
                unitOfWork.add(mapper.getId(entity), entity, dbObject);
                result.add(entity);
            }
            return result;
        } finally {
            cursor.close();
        }

    }

    public void save(Object element) {
        EntityMapper<?> mapper = entityMapper(element.getClass());
        DBObject dbObject = mapper.toDBObject(element);
        db.getCollection(mapper.collectionName()).insert(dbObject);
        mapper.populateId(element, dbObject);
        unitOfWork.add(mapper.getId(element), element, dbObject);
    }

    public void update(Object element) {
        EntityMapper<?> mapper = entityMapper(element.getClass());
        DBCollection collection = db.getCollection(mapper.collectionName());
        DBObject update = mapper.toDBObject(element);
        DBObject query = new BasicDBObject();
        query.put("_id", update.get("_id"));
        collection.update(query, update);
    }

    public EntityMapper<?> entityMapper(Class<?> type) {
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
        return criteriaFactory.create(type, this);
    }

    private final DB db;
    private MapperContext context;
    private final UnitOfWork unitOfWork = new UnitOfWork(this);
    private CriteriaFactory criteriaFactory;
}
