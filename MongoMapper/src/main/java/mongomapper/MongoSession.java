package mongomapper;


import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class MongoSession {

    private DB db;
    private MappingContext context;

    public MongoSession(DB db) {
        this.db = db;
    }

    public void start() {
        db.requestStart();
    }

    public void stop() {
        db.requestDone();
    }

    public void setMappingContext(MappingContext context) {
        this.context = context;
    }

    public <T> T get(String id, Class<T> entityType) {
        DBCollection collection = db.getCollection(collectionName(entityType));
        Object dbId = context.mapperFor(entityType).getDbId(id);
        DBObject query = new BasicDBObject("_id", dbId);
        DBObject result = collection.findOne(query);
        return (T) context.mapperFor(entityType).toInstance(result);
    }

    public void save(Object element) {
        DBObject dbObject = context.mapperFor(element.getClass()).toDBObject(element);
        db.getCollection(collectionName(element.getClass())).insert(dbObject);
    }

    public void update(Object element) {
        DBCollection collection = db.getCollection(collectionName(element.getClass()));
        Mapper<?> mapper = context.mapperFor(element.getClass());
        DBObject update = mapper.toDBObject(element);
        DBObject query = new BasicDBObject();
        query.put("_id", update.get("_id"));
        collection.update(query, update);
    }

    private <T>String collectionName(Class<T> clazz) {
        return clazz.getSimpleName().toLowerCase();
    }
}
