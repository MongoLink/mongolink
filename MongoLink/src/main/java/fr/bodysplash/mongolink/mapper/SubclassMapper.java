package fr.bodysplash.mongolink.mapper;


import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import java.util.Map;

public class SubclassMapper<T> extends Mapper<T> {

    public SubclassMapper(Class<T> type) {
        super(type);
    }

    @Override
    protected void doPopulate(T instance, DBObject from) {
        parentMapper.populate(from, instance);
    }

    @Override
    protected void doSave(Object element, BasicDBObject object) {
        parentMapper.save(element, object);
        object.put("__discriminator", discriminator());
    }

    private String discriminator() {
        return getPersistentType().getSimpleName();
    }

    public void setParentMapper(Mapper<?> parentMapper) {
        this.parentMapper = parentMapper;
    }

    private Mapper<?> parentMapper;
}
