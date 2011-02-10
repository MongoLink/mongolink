package fr.bodysplash.mongomapper;


import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class IdMapper {

    private String name;
    private static final Logger LOGGER = Logger.getLogger(PropertyMapper.class);
    private Method method;
    private Mapper<?> mapper;

    public IdMapper(String name, Method method) {
        this.name = name;
        this.method = method;
    }

    protected String dbFieldName() {
        return "_id";
    }

    public void saveTo(Object element, BasicDBObject object) {
        try {
            object.put(dbFieldName(), new ObjectId(method.invoke(element).toString()));
        } catch (Exception e) {
            LOGGER.error("Can't saveInto property " + name, e);
        }
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
}
