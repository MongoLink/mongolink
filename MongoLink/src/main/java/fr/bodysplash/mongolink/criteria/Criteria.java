package fr.bodysplash.mongolink.criteria;

import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import fr.bodysplash.mongolink.MongoSession;

import java.util.Collections;
import java.util.List;

public class Criteria<T> {

    public Criteria(Class<T> entityType, MongoSession mongoSession) {
        this.entityType = entityType;
        this.mongoSession = mongoSession;
    }

    public Class<?> getEntityType() {
        return entityType;
    }

    public List<T> list() {
        return mongoSession.executeQuery(entityType, createQuery());
    }

    public void add(Restriction restriction) {
        restrictions.add(restriction);
    }

    DBObject createQuery() {
        final BasicDBObject result = new BasicDBObject();
        for (Restriction restriction : restrictions) {
            restriction.apply(result);
        }
        return result;
    }

    protected MongoSession getMongoSession() {
        return mongoSession;
    }

    protected List<Restriction> getRestrictions() {
        return Collections.unmodifiableList(restrictions);
    }

    private Class<T> entityType;
    private MongoSession mongoSession;
    private List<Restriction> restrictions = Lists.newArrayList();
}
