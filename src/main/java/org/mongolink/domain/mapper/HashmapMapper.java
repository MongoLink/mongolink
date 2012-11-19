package org.mongolink.domain.mapper;

import com.mongodb.*;
import org.apache.log4j.Logger;
import org.mongolink.utils.*;

import java.lang.reflect.*;
import java.util.Map;

public class HashmapMapper implements Mapper {

    public HashmapMapper(MethodContainer methodContainer) {
        this.name = methodContainer.shortName();
        this.method = methodContainer.getMethod();
    }

    @Override
    public void save(final Object instance, final DBObject into) {
        try {
            Map map = (Map) method.invoke(instance);
            into.put(name, new BasicDBObject(map));
        } catch (Exception e) {
            LOGGER.error("Can't saveInto collection " + name, e);
        }
    }

    @Override
    public void populate(final Object instance, final DBObject from) {
        try {
            Field field = ReflectionUtils.findPrivateField(instance.getClass(), name);
            field.setAccessible(true);
            Map map = (Map) field.get(instance);
            map.putAll((Map) from.get(name));
            field.setAccessible(false);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private MapperContext context() {
        return mapper.getContext();
    }

    public void setMapper(ClassMapper<?> mapper) {
        this.mapper = mapper;
    }

    private final Method method;
    private final String name;
    private ClassMapper<?> mapper;
    private static final Logger LOGGER = Logger.getLogger(CollectionMapper.class);
}
