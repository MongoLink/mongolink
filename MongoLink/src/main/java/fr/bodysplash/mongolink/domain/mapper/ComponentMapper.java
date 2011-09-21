package fr.bodysplash.mongolink.domain.mapper;

import com.mongodb.*;

public class ComponentMapper<T> extends Mapper<T> {

    public ComponentMapper(Class<T> type) {
        super(type);
    }

    @Override
    protected void doPopulate(T instance, DBObject from) {
    }

    @Override
    protected void doSave(Object element, BasicDBObject object) {
    }
}
