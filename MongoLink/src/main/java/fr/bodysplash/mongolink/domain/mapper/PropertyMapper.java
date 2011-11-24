package fr.bodysplash.mongolink.domain.mapper;


import com.mongodb.DBObject;
import fr.bodysplash.mongolink.MongoLinkException;
import fr.bodysplash.mongolink.domain.converter.Converter;
import fr.bodysplash.mongolink.utils.*;
import org.apache.log4j.Logger;

import java.lang.reflect.*;

class PropertyMapper implements Mapper {

    public PropertyMapper(MethodContainer method) {
        this.name = method.shortName();
        this.method = method.getMethod();
    }

    @Override
    public void save(Object instance, DBObject into) {
        into.put(dbFieldName(), converter().toDbValue(getValue(instance)));
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

    @Override
    public void populate(Object instance, DBObject from) {
        try {
            Field field = ReflectionUtils.findPrivateField(mapper.getPersistentType(), name);
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

    public void setMapper(ClassMapper<?> mapper) {
        this.mapper = mapper;
    }

    private final String name;
    private static final Logger LOGGER = Logger.getLogger(PropertyMapper.class);
    private final Method method;
    private ClassMapper<?> mapper;
}
