package fr.bodysplash.mongomapper;


import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;

public class CollectionMapper {

    private Method method;
    private String name;
    private Mapper<?> mapper;
    private static final Logger LOGGER = Logger.getLogger(CollectionMapper.class);

    public CollectionMapper(String name, Method method) {
        this.name = name;
        this.method = method;
    }

    public void saveInto(Object element, BasicDBObject object) {
        try {
            Collection collection = (Collection) method.invoke(element);
            BasicDBList list = new BasicDBList();
            for (Object child : collection) {
                DBObject childObject = context().mapperFor(child.getClass()).toDBObject(child);
                list.add(childObject);
            }
            object.put(name, list);
        } catch (Exception e) {
            LOGGER.error("Can't saveInto collection " + name, e);
        }
    }

    private MappingContext context() {
        return mapper.getContext();
    }

    public void populateFrom(Object instance, DBObject from) {
        try {
            Field field = instance.getClass().getDeclaredField(name);
            field.setAccessible(true);
            ParameterizedType gen = (ParameterizedType) field.getGenericType();
            Mapper<?> childMapper = context().mapperFor((Class<?>) gen.getActualTypeArguments()[0]);
            BasicDBList list = (BasicDBList) from.get(name);
            if (list != null) {
                Collection collection = (Collection) field.get(instance);
                for (Object o : list) {
                    DBObject childObject = (DBObject) o;
                    collection.add(childMapper.toInstance(childObject));
                }
            }
            field.setAccessible(false);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public void setMapper(Mapper<?> mapper) {
        this.mapper = mapper;
    }
}
