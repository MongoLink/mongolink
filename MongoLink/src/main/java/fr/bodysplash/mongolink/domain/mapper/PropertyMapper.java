package fr.bodysplash.mongolink.domain.mapper;


import com.mongodb.DBObject;
import fr.bodysplash.mongolink.MongoLinkException;
import fr.bodysplash.mongolink.domain.converter.Converter;
import fr.bodysplash.mongolink.utils.MethodContainer;
import fr.bodysplash.mongolink.utils.ReflectionUtils;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

class PropertyMapper implements Mapper {

    public PropertyMapper(MethodContainer method) {
        this.name = method.shortName();
        this.method = method.getMethod();
    }

    @Override
    public void save(Object instance, DBObject into) {
        final Object propertyValue = getPropertyValue(instance);
        if (propertyValue != null) {
            into.put(dbFieldName(), converter().toDbValue(propertyValue));
        }
    }

    private Converter converter() {
        return getMapper().getContext().converterFor(method.getReturnType());
    }

    protected Object getPropertyValue(Object element) {
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
            field.set(instance, valueFrom(from));
            field.setAccessible(false);
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    protected Object valueFrom(DBObject from) {
        return converter().fromDbValue(from.get(dbFieldName()));
    }

    String dbFieldName() {
        return name;
    }

    public void setMapper(ClassMapper<?> mapper) {
        this.mapper = mapper;
    }

    protected ClassMapper<?> getMapper() {
        return mapper;
    }

    private final String name;
    private static final Logger LOGGER = Logger.getLogger(PropertyMapper.class);
    private final Method method;
    private ClassMapper<?> mapper;
}
