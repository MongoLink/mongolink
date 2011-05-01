package fr.bodysplash.mongolink.mapper;


import com.google.common.collect.Maps;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import java.util.Map;

public class EntityMapper<T> extends Mapper<T> {

    EntityMapper(Class<T> persistentType) {
        super(persistentType);
    }

    @Override
    protected void doPopulate(T instance, DBObject from) {
        populateId(instance, from);
    }

    public void populateId(Object element, DBObject dbObject) {
        idMapper.populateFrom(element, dbObject);
    }

    @Override
    protected void doSave(Object element, BasicDBObject object) {
        idMapper.saveTo(element, object);
    }

    private boolean hasId() {
        return idMapper != null;
    }

    public void setId(IdMapper idMapper) {
        idMapper.setMapper(this);
        this.idMapper = idMapper;
    }

    public Object getDbId(String id) {
        return idMapper.getDbValue(id);
    }

    public <U> void addSubclass(SubclassMapper<U> mapper) {
        mapper.setParentMapper(this);
        subclasses.put(mapper.discriminator(), mapper);
    }

    @Override
    public T toInstance(DBObject from) {
        String discriminator = SubclassMapper.discriminatorValue(from);
        if(subclasses.get(discriminator) != null) {
            return (T) subclasses.get(discriminator).toInstance(from);
        }
        return super.toInstance(from);
    }

    private IdMapper idMapper;
    private Map<String, SubclassMapper<?>> subclasses = Maps.newHashMap();
}
