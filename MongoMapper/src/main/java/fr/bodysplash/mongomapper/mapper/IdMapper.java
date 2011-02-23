package fr.bodysplash.mongomapper.mapper;


import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class IdMapper {

    private final String name;
    private final IdGeneration generationStrategy;
    private static final Logger LOGGER = Logger.getLogger(PropertyMapper.class);
    private final Method method;
    private Mapper<?> mapper;

    public IdMapper(String name, Method method, IdGeneration generationStrategy) {
        this.method = method;
        this.name = name;
        this.generationStrategy = generationStrategy;
    }

    String dbFieldName() {
        return "_id";
    }

    public void saveTo(Object element, BasicDBObject object) {
        try {
            object.put(dbFieldName(), getIdValue(element));
        } catch (Exception e) {
            LOGGER.error("Can't saveInto property " + name, e);
        }
    }

    private Object getIdValue(Object element) throws IllegalAccessException, InvocationTargetException {
        Object keyValue = method.invoke(element);
        if (generationStrategy == IdGeneration.Auto && keyValue != null) {
            return new ObjectId(keyValue.toString());
        }
        return keyValue;
    }

    public void populateFrom(Object instance, DBObject from) {
        try {
            Field field = mapper.getPersistentType().getDeclaredField(name);
            field.setAccessible(true);
            Object value = from.get(dbFieldName());
            field.set(instance, value.toString());
            field.setAccessible(false);
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    public void setMapper(Mapper<?> mapper) {
        this.mapper = mapper;
    }

    public Object getDbValue(String id) {
        if (generationStrategy == IdGeneration.Natural) {
            return id;
        }
        return new ObjectId(id);
    }
}
