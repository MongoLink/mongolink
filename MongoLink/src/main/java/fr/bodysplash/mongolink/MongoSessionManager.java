package fr.bodysplash.mongolink;

import com.mongodb.DB;
import fr.bodysplash.mongolink.criteria.CriteriaFactory;
import fr.bodysplash.mongolink.mapper.ContextBuilder;
import fr.bodysplash.mongolink.mapper.MapperContext;

public class MongoSessionManager {


    public static MongoSessionManager create(ContextBuilder contextBuilder, Settings settings) {
        MongoSessionManager manager = new MongoSessionManager(contextBuilder.createContext());
        manager.settings = settings;
        manager.dbFactory = settings.createDbFactory();
        return manager;
    }

    private MongoSessionManager(MapperContext mapperContext) {
        this.mapperContext = mapperContext;
    }

    public MongoSession createSession() {
        MongoSession mongoSession = new MongoSession(getDb(), getCriteriaFactory());
        mongoSession.setMappingContext(mapperContext);
        return mongoSession;
    }

    private CriteriaFactory getCriteriaFactory() {
        return settings.getCriteriaFactory();
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

    private final MapperContext mapperContext;
    private DbFactory dbFactory;
    private Settings settings;
}
