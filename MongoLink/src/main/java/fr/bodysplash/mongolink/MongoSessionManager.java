package fr.bodysplash.mongolink;

import com.mongodb.DB;
import com.mongodb.DBAddress;
import fr.bodysplash.mongolink.mapper.ContextBuilder;
import fr.bodysplash.mongolink.mapper.MapperContext;

public class MongoSessionManager {

    private  DBAddress db;
    private final MapperContext mapperContext;
    private DbFactory dbFactory;
    private Settings settings;

    public static MongoSessionManager create(ContextBuilder contextBuilder, Settings db) {
        MongoSessionManager manager = new MongoSessionManager(contextBuilder.createContext());
        manager.settings = db;
        manager.dbFactory = db.createDbFactory();
        return manager;
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
        return dbFactory.get(settings.getDbName());
    }

    public MapperContext getMapperContext() {
        return mapperContext;
    }

    public void close() {
        dbFactory.close();
    }
}
