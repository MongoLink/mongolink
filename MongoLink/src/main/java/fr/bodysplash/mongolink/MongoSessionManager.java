package fr.bodysplash.mongolink;

import com.mongodb.DB;
import com.mongodb.DBAddress;
import fr.bodysplash.mongolink.mapper.ContextBuilder;
import fr.bodysplash.mongolink.mapper.MapperContext;

public class MongoSessionManager {

    private static DBAddress db;
    private final MapperContext mapperContext;
    private DbFactory dbFactory = new DbFactory();

    public static MongoSessionManager create(ContextBuilder contextBuilder, DBAddress db) {
        MongoSessionManager.db = db;
        return new MongoSessionManager(contextBuilder.createContext());
    }

    private MongoSessionManager(MapperContext mapperContext) {
        this.mapperContext = mapperContext;
    }

    public MongoSession createSession() {
        MongoSession mongoSession = new MongoSession(getDb());
        mongoSession.setMappingContext(mapperContext);
        return mongoSession;
    }

    private DB getDb() {
        return dbFactory.get(db);
    }

    public MapperContext getMapperContext() {
        return mapperContext;
    }

    public void setDbFactory(DbFactory dbFactory) {
        this.dbFactory = dbFactory;
    }

    public void close() {
        dbFactory.close();
    }
}
