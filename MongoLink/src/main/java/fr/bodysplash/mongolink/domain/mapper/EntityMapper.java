package fr.bodysplash.mongolink.domain.mapper;


import com.google.common.collect.Maps;
import com.mongodb.*;

import java.util.Map;

@SuppressWarnings("unchecked")
public class EntityMapper<T> extends ClassMapper<T> {

    public EntityMapper(Class<T> persistentType) {
        super(persistentType);
    }

    @Override
    protected void doPopulate(T instance, DBObject from) {
        populateId(instance, from);
    }

    public void populateId(Object element, DBObject dbObject) {
        idMapper.populate(element, dbObject);
    }

    @Override
    protected void doSave(Object element, DBObject object) {
        idMapper.save(element, object);
    }

    void setId(IdMapper idMapper) {
        idMapper.setMapper(this);
        this.idMapper = idMapper;
    }

    public Object getDbId(String id) {
        return idMapper.convertToDbValue(id);
    }

    public Object getId(Object entity) {
        return idMapper.getIdValue(entity);
    }

    public Object getId(DBObject dbo) {
        return idMapper.getIdValue(dbo);
    }

    <U> void addSubclass(SubclassMapper<U> mapper) {
        mapper.setParentMapper(this);
        subclasses.put(mapper.discriminator(), mapper);
    }

    void addReference() {
        //To change body of created methods use File | Settings | File Templates.
    }

    @Override
    public T toInstance(DBObject from) {
        String discriminator = SubclassMapper.discriminatorValue(from);
        if (subclasses.get(discriminator) != null) {
            return (T) subclasses.get(discriminator).toInstance(from);
        }
        return super.toInstance(from);
    }

    @Override
    public DBObject toDBObject(Object element) {
        if (isSubclass(element)) {
            return subclassMapperFor(element).toDBObject(element);
        }
        return super.toDBObject(element);
    }

    private boolean isSubclass(Object element) {
        return subclassMapperFor(element) != null;
    }

    private SubclassMapper<?> subclassMapperFor(Object element) {
        for (SubclassMapper<?> subclassMapper : subclasses.values()) {
            if (subclassMapper.getPersistentType().isAssignableFrom(element.getClass())) {
                return subclassMapper;
            }
        }
        return null;
    }

    public String collectionName() {
        return getPersistentType().getSimpleName().toLowerCase();
    }

    private IdMapper idMapper;
    private Map<String, SubclassMapper<?>> subclasses = Maps.newHashMap();
}
