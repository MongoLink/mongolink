package fr.bodysplash.mongomapper;


import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import net.sf.cglib.core.ReflectUtils;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.util.List;

public class Mapper<T> {

    private Class<T> persistentType;
    private List<PropertyMapper> properties = Lists.newArrayList();
    private List<CollectionMapper> collections = Lists.newArrayList();
    private MappingContext context;
    private static final Logger LOGGER = Logger.getLogger(Mapper.class);

    Mapper(Class<T> persistentType) {
        this.persistentType = persistentType;
    }

    public Class<T> getPersistentType() {
        return persistentType;
    }

    void setContext(MappingContext context) {
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
        return instance;
    }

    private T makeInstance() {
        return (T) ReflectUtils.newInstance(persistentType);
    }

    private void populateProperties(T instance, DBObject from) {
        try {
            for (PropertyMapper property : properties) {
                property.populateFrom(instance, from);
            }
            if(hasId(instance)) {
                PropertyMapper id = new PropertyMapper("_id", null);
                id.setMapper(this);
                id.populateFrom(instance, from);
            }
        } catch (Exception e) {
            LOGGER.error("Can't populateFrom properties", e);
        }
    }

    private boolean hasId(T instance) {
        try {
            Field field = persistentType.getDeclaredField("_id");
        } catch (NoSuchFieldException e) {
            return false;
        }
        return true;
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

    public MappingContext getContext() {
        return context;
    }

}
