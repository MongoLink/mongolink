package fr.bodysplash.mongolink;


import com.mongodb.*;
import fr.bodysplash.mongolink.domain.*;
import fr.bodysplash.mongolink.domain.criteria.*;
import fr.bodysplash.mongolink.domain.mapper.*;
import fr.bodysplash.mongolink.domain.updateStategy.*;

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

    protected void setMappingContext(MapperContext context) {
        this.context = context;
    }

    public <T> T get(String id, Class<T> entityType) {
        EntityMapper<T> mapper = (EntityMapper<T>) entityMapper(entityType);
        Object dbId = mapper.getDbId(id);
        if (unitOfWork.contains(entityType, dbId)) {
            return unitOfWork.getEntity(entityType, dbId);
        }
        DBObject query = new BasicDBObject("_id", dbId);
        return (T) createExecutor(mapper).executeUnique(query);
    }

    public <T> List<T> getAll(Class<T> entityType) {
        return createExecutor(entityMapper(entityType)).execute(new BasicDBObject());
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
        final DBObject initialValue = unitOfWork.getDBOBject(element.getClass(), mapper.getId(element));
        DBObject update = mapper.toDBObject(element);
        DBCollection collection = db.getCollection(mapper.collectionName());
        updateStrategy.update(initialValue, update, collection);
        unitOfWork.update(mapper.getId(element), element, update);
    }

    public DB getDb() {
        return db;
    }

    public Criteria createCriteria(Class<?> type) {
        return criteriaFactory.create(createExecutor(entityMapper(type)));
    }

    public UpdateStrategy getUpdateStrategy() {
        return updateStrategy;
    }

    public void setUpdateStrategy(UpdateStrategies updateStrategy) {
        this.updateStrategy = updateStrategy.instance();
    }

    public void clear() {
        unitOfWork.clear();
    }

    private QueryExecutor createExecutor(EntityMapper<?> mapper) {
        return new QueryExecutor(db, mapper, unitOfWork);
    }

    private EntityMapper<?> entityMapper(Class<?> type) {
        checkIsAnEntity(type);
        return (EntityMapper<?>) context.mapperFor(type);
    }

    private void checkIsAnEntity(Class<?> entityType) {
        ClassMapper<?> mapper = context.mapperFor(entityType);
        if (mapper == null || !(mapper instanceof EntityMapper)) {
            throw new MongoLinkError(entityType.getName() + " is not an entity");
        }
    }

    private final DB db;
    private MapperContext context;
    private final UnitOfWork unitOfWork = new UnitOfWork(this);
    private CriteriaFactory criteriaFactory;
    private UpdateStrategy updateStrategy = new OverwriteStrategy();
}
