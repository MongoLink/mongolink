package fr.bodysplash.mongolink.mapper;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

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
