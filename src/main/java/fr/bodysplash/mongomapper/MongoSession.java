package fr.bodysplash.mongomapper;


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

    public <T> T get(String id, Class<T> clazz) {
        DBCollection collection = db.getCollection(collectionName(clazz));
        DBObject query = new BasicDBObject("_id", id);
        DBObject result = collection.findOne(query);
        return (T) context.mapperFor(clazz).toInstance(result);
    }

    public void save(Object element) {
        DBObject dbObject = context.mapperFor(element.getClass()).toDBObject(element);
        db.getCollection(collectionName(element.getClass())).insert(dbObject);
    }

    private <T>String collectionName(Class<T> clazz) {
        return clazz.getSimpleName().toLowerCase();
    }
}
