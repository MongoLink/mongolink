package fr.bodysplash.mongolink.mapper;


import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import fr.bodysplash.mongolink.MongoLinkException;
import fr.bodysplash.mongolink.converter.Converter;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

class PropertyMapper {

    private final String name;
    private static final Logger LOGGER = Logger.getLogger(PropertyMapper.class);
    private final Method method;
    private Mapper<?> mapper;


    public PropertyMapper(String name, Method method) {
        this.name = name;
        this.method = method;
    }

    public void saveTo(Object element, BasicDBObject object) {
        object.put(dbFieldName(), converter().toDbValue(getValue(element)));
    }

    private Converter converter() {
        return Converter.forMethod(method);
    }

    private Object getValue(Object element) {
        try {
            return method.invoke(element);
        } catch (Exception e) {
            LOGGER.warn("Can't get value from " + method.getName());
            throw new MongoLinkException(e);
        }
    }

    public void populateFrom(Object instance, DBObject from) {
        try {
            Field field = mapper.getPersistentType().getDeclaredField(name);
            field.setAccessible(true);
            Object value = from.get(dbFieldName());
            field.set(instance, converter().fromDbValue(value));
            field.setAccessible(false);
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    String dbFieldName() {
        return name;
    }

    public void setMapper(Mapper<?> mapper) {
        this.mapper = mapper;
    }
}
