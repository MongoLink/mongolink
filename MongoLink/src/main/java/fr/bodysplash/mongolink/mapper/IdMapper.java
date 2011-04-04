package fr.bodysplash.mongolink.mapper;


import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import fr.bodysplash.mongolink.utils.MethodContainer;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class IdMapper {

    public IdMapper(MethodContainer methodContainer, IdGeneration auto) {
        this.method = methodContainer.getMethod();
        this.name = methodContainer.shortName();
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

    public void setMapper(EntityMapper<?> mapper) {
        this.mapper = mapper;
    }

    public Object getDbValue(String id) {
        if (generationStrategy == IdGeneration.Natural) {
            return id;
        }
        return new ObjectId(id);
    }

    public void natural() {
        generationStrategy = IdGeneration.Natural;
    }

    public void auto() {
        generationStrategy = IdGeneration.Auto;
    }

    private final String name;
    private IdGeneration generationStrategy;
    private static final Logger LOGGER = Logger.getLogger(IdMapper.class);
    private final Method method;
    private EntityMapper<?> mapper;
}
