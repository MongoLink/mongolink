package fr.bodysplash.mongomapper;


import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class PropertyMapper {
    
    private String name;
    private static final Logger LOGGER = Logger.getLogger(PropertyMapper.class);
    private Method method;
    private Mapper<?> mapper;

    public PropertyMapper(String name, Method method) {
        this.name = name;
        this.method = method;
    }

    public void saveTo(Object element, BasicDBObject object) {
        try {
            object.put(dbFieldName(), method.invoke(element));
        } catch (Exception e) {
            LOGGER.error("Can't saveInto property " + name, e);
        }
    }

    public void populateFrom(Object instance, DBObject from) {
        try {
            Field field = mapper.getPersistentType().getDeclaredField(name);
            field.setAccessible(true);
            field.set(instance, from.get(dbFieldName()));
            field.setAccessible(false);
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    protected String dbFieldName() {
        return name;
    }

    public void setMapper(Mapper<?> mapper) {
        this.mapper = mapper;
    }
}
