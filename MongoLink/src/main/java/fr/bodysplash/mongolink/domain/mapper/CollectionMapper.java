package fr.bodysplash.mongolink.domain.mapper;


import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import fr.bodysplash.mongolink.utils.MethodContainer;
import fr.bodysplash.mongolink.utils.ReflectionUtils;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;

class CollectionMapper implements Mapper {

    public CollectionMapper(MethodContainer methodContainer) {
        this.name = methodContainer.shortName();
        this.method = methodContainer.getMethod();
    }

    @Override
    public void save(Object instance, DBObject into) {
        try {
            Collection collection = (Collection) method.invoke(instance);
            BasicDBList list = new BasicDBList();
            for (Object child : collection) {
                DBObject childObject = context().mapperFor(child.getClass()).toDBObject(child);
                list.add(childObject);
            }
            into.put(name, list);
        } catch (Exception e) {
            LOGGER.error("Can't saveInto collection " + name, e);
        }
    }

    @Override
    public void populate(Object instance, DBObject from) {
        try {
            Field field = ReflectionUtils.findPrivateField(instance.getClass(), name);
            field.setAccessible(true);
            ParameterizedType elementType = (ParameterizedType) field.getGenericType();
            ClassMapper<?> childMapper = context().mapperFor((Class<?>) elementType.getActualTypeArguments()[0]);
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

    public void saveInto(Object element, DBObject object) {
        save(element, object);
    }

    private MapperContext context() {
        return mapper.getContext();
    }

    public void populateFrom(Object instance, DBObject from) {
        populate(instance, from);
    }

    public void setMapper(ClassMapper<?> mapper) {
        this.mapper = mapper;
    }

    private final Method method;
    private final String name;
    private ClassMapper<?> mapper;
    private static final Logger LOGGER = Logger.getLogger(CollectionMapper.class);
}
