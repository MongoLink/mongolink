package fr.bodysplash.mongolink.mapper;

import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import net.sf.cglib.core.ReflectUtils;
import org.apache.log4j.Logger;

import java.util.List;

public abstract class Mapper<T> {
    
    public Mapper(Class<T> persistentType) {
        this.persistentType = persistentType;
    }

    public Class<T> getPersistentType() {
        return persistentType;
    }

    void setContext(MapperContext context) {
        this.context = context;
    }

    void addCollection(CollectionMapper collection) {
        collection.setMapper(this);
        collections.add(collection);
    }

    public void addProperty(PropertyMapper property) {
        property.setMapper(this);
        properties.add(property);
    }

    public T toInstance(DBObject from) {
        T instance = makeInstance();
        populateProperties(instance, from);
        populateCollections(instance, from);
        doPopulate(instance, from);
        return instance;
    }

    private T makeInstance() {
        return (T) ReflectUtils.newInstance(persistentType);
    }

    protected abstract void doPopulate(T instance, DBObject from);

    private void populateProperties(T instance, DBObject from) {
        try {
            for (PropertyMapper property : properties) {
                property.populateFrom(instance, from);
            }
        } catch (Exception e) {
            LOGGER.error("Can't populateFrom properties", e);
        }
    }

    private void populateCollections(T instance, DBObject from) {
        for (CollectionMapper collection : collections) {
            collection.populateFrom(instance, from);
        }
    }

    public DBObject toDBObject(Object element) {
        BasicDBObject object = new BasicDBObject();
        saveProperties(element, object);
        saveCollections(element, object);
        doSave(element, object);
        return object;
    }

    private void saveCollections(Object element, BasicDBObject object) {
        for (CollectionMapper collection : collections) {
            collection.saveInto(element, object);
        }
    }

    private void saveProperties(Object element, BasicDBObject object) {
        for (PropertyMapper propertyMapper : properties) {
            propertyMapper.saveTo(element, object);
        }

    }

    protected abstract void doSave(Object element, BasicDBObject object);

    public MapperContext getContext() {
        return context;
    }

    private static final Logger LOGGER = Logger.getLogger(EntityMapper.class);
    protected final Class<T> persistentType;
    private final List<PropertyMapper> properties = Lists.newArrayList();
    private final List<CollectionMapper> collections = Lists.newArrayList();
    private MapperContext context;
}
