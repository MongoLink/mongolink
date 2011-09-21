package fr.bodysplash.mongolink.domain.mapper;


import com.mongodb.*;
import fr.bodysplash.mongolink.utils.*;
import org.apache.log4j.Logger;

import java.lang.reflect.*;
import java.util.Collection;

class CollectionMapper {

    public CollectionMapper(MethodContainer methodContainer) {
        this.name = methodContainer.shortName();
        this.method = methodContainer.getMethod();
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

    private MapperContext context() {
        return mapper.getContext();
    }

    public void populateFrom(Object instance, DBObject from) {
        try {
            Field field = ReflectionUtils.findPrivateField(instance.getClass(), name);
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

    private final Method method;
    private final String name;
    private Mapper<?> mapper;
    private static final Logger LOGGER = Logger.getLogger(CollectionMapper.class);
}
