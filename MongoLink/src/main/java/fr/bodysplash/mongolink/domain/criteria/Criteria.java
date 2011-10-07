package fr.bodysplash.mongolink.domain.criteria;

import com.google.common.collect.Lists;
import com.mongodb.*;
import fr.bodysplash.mongolink.MongoSession;

import java.util.*;

public class Criteria<T> {

    public Criteria(Class<T> entityType, MongoSession mongoSession) {
        this.entityType = entityType;
        this.mongoSession = mongoSession;
        this.skipNumber = 0;
        this.limitNumber = 0;
    }

    public Criteria(Class<T> entityType, MongoSession mongoSession, int skipNumber, int limitNumber) {
        this.entityType = entityType;
        this.mongoSession = mongoSession;
        this.skipNumber = skipNumber;
        this.limitNumber = limitNumber;
    }

    public Class<?> getEntityType() {
        return entityType;
    }

    public List<T> list() {
        return mongoSession.executeQuery(entityType, createQuery(), skipNumber, limitNumber);
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
    private int skipNumber;
    private int limitNumber;
}
