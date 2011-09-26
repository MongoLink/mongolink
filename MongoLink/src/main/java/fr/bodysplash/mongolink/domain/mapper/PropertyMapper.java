package fr.bodysplash.mongolink.domain.mapper;


import com.mongodb.*;
import fr.bodysplash.mongolink.MongoLinkException;
import fr.bodysplash.mongolink.domain.converter.Converter;
import fr.bodysplash.mongolink.utils.*;
import org.apache.log4j.Logger;

import java.lang.reflect.*;

class PropertyMapper {

    public PropertyMapper(MethodContainer method) {
        this.name = method.shortName();
        this.method = method.getMethod();
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

    public void setMapper(Mapper<?> mapper) {
        this.mapper = mapper;
    }

    private final String name;
    private static final Logger LOGGER = Logger.getLogger(PropertyMapper.class);
    private final Method method;
    private Mapper<?> mapper;
}
