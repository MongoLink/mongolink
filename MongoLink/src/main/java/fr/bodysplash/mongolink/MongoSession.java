package fr.bodysplash.mongolink;

import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import fr.bodysplash.mongolink.domain.QueryExecutor;
import fr.bodysplash.mongolink.domain.UnitOfWork;
import fr.bodysplash.mongolink.domain.UpdateStrategies;
import fr.bodysplash.mongolink.domain.criteria.Criteria;
import fr.bodysplash.mongolink.domain.criteria.CriteriaFactory;
import fr.bodysplash.mongolink.domain.mapper.ClassMapper;
import fr.bodysplash.mongolink.domain.mapper.EntityMapper;
import fr.bodysplash.mongolink.domain.mapper.MapperContext;
import fr.bodysplash.mongolink.domain.updateStategy.OverwriteStrategy;
import fr.bodysplash.mongolink.domain.updateStategy.UpdateStrategy;

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
		getDbCollection(mapper).insert(dbObject);
		mapper.populateId(element, dbObject);
		unitOfWork.add(mapper.getId(element), element, dbObject);
	}

	public void update(Object element) {
		EntityMapper<?> mapper = entityMapper(element.getClass());
		DBObject initialValue = unitOfWork.getDBOBject(element.getClass(), mapper.getId(element));
		DBObject update = mapper.toDBObject(element);
		updateStrategy.update(initialValue, update, getDbCollection(mapper));
		unitOfWork.update(mapper.getId(element), element, update);
	}

	public void delete(Object element) {
		EntityMapper<?> mapper = entityMapper(element.getClass());
		DBObject value = unitOfWork.getDBOBject(element.getClass(), mapper.getId(element));
		getDbCollection(mapper).remove(value);
		unitOfWork.delete(mapper.getId(element), element);
	}

	private DBCollection getDbCollection(EntityMapper<?> mapper) {
		return db.getCollection(mapper.collectionName());
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
