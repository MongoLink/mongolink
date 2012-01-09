package fr.bodysplash.mongolink;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import fr.bodysplash.mongolink.domain.criteria.CriteriaFactory;
import fr.bodysplash.mongolink.domain.mapper.ClassMapper;
import fr.bodysplash.mongolink.domain.mapper.ContextBuilder;
import fr.bodysplash.mongolink.domain.mapper.EntityMapper;
import fr.bodysplash.mongolink.domain.mapper.MapperContext;

public class MongoSessionManager {

    public static MongoSessionManager create(ContextBuilder contextBuilder, Settings settings) {
        MongoSessionManager manager = new MongoSessionManager(contextBuilder.createContext());
        manager.settings = settings;
        manager.dbFactory = settings.createDbFactory();
        manager.createCappedCollections();
        return manager;
    }

    private MongoSessionManager(MapperContext mapperContext) {
        this.mapperContext = mapperContext;
    }

    private void createCappedCollections() {
        for (ClassMapper mapper : mapperContext.getMappers()) {
            if (mapper.isCapped()) {
                EntityMapper<?> entityMapper = (EntityMapper<?>) mapper;
                final MongoSession session = createSession();
                if (!session.getDb().collectionExists(entityMapper.collectionName())) {
                    session.start();
                    final DBObject options = new BasicDBObject();
                    options.put("capped", "true");
                    options.put("size", entityMapper.getCappedSize());
                    options.put("max", entityMapper.getCappedMax());
                    session.getDb().createCollection(entityMapper.collectionName(), options);
                    session.stop();
                }
            }
        }
    }

    public MongoSession createSession() {
        MongoSession mongoSession = new MongoSession(getDb(), getCriteriaFactory());
        mongoSession.setMappingContext(mapperContext);
        mongoSession.setUpdateStrategy(settings.getUpdateStrategy());
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
