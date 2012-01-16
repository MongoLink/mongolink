package fr.bodysplash.mongolink.domain.mapper;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import fr.bodysplash.mongolink.utils.MethodContainer;
import org.apache.log4j.Logger;

public class PropertyComponentMapper extends PropertyMapper {

    public PropertyComponentMapper(Class<?> type, MethodContainer methodContainer) {
        super(methodContainer);
        this.type = type;
    }

    public ComponentMapper<?> find() {
        return (ComponentMapper<?>) context().mapperFor(type);
    }

    private MapperContext context() {
        return getMapper().getContext();
    }

    @Override
    public void save(Object instance, DBObject into) {
        BasicDBObject child = new BasicDBObject();
        final Object propertyValue = getPropertyValue(instance);
        if (propertyValue != null) {
            find().save(propertyValue, child);
            into.put(dbFieldName(), child);
        }
    }

    @Override
    protected Object valueFrom(DBObject from) {
        try {
            final Object instance = type.newInstance();
            find().populate(instance, (DBObject) from.get(dbFieldName()));
            return instance;
        } catch (Exception e) {
            LOGGER.error("Can't create component", e);
            return null;
        }
    }

    private Class<?> type;
    public static final Logger LOGGER = Logger.getLogger(PropertyComponentMapper.class);
}
