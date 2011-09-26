package fr.bodysplash.mongolink.domain.mapper;


import com.mongodb.*;
import fr.bodysplash.mongolink.MongoLinkError;
import fr.bodysplash.mongolink.utils.MethodContainer;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;

import java.lang.reflect.*;

public class IdMapper {

    public IdMapper(MethodContainer methodContainer, IdGeneration generationStrategy) {
        this.method = methodContainer.getMethod();
        this.name = methodContainer.shortName();
        this.generationStrategy = generationStrategy;
    }

    String dbFieldName() {
        return "_id";
    }

    public void saveTo(Object entity, BasicDBObject object) {
        try {
            object.put(dbFieldName(), getIdValue(entity));
        } catch (Exception e) {
            LOGGER.error("Can't saveInto property " + name, e);
        }
    }

    protected Object getIdValue(Object element) {
        try {
            Object keyValue = method.invoke(element);
            if (generationStrategy == IdGeneration.Auto && keyValue != null) {
                return new ObjectId(keyValue.toString());
            }
            return keyValue;
        } catch (Exception e) {
            throw new MongoLinkError("Can't get id value", e);
        }
    }

    public void populateFrom(Object instance, DBObject from) {
        try {
            Field field = mapper.getPersistentType().getDeclaredField(name);
            field.setAccessible(true);
            Object value = getIdValue(from);
            field.set(instance, value.toString());
            field.setAccessible(false);
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    protected Object getIdValue(DBObject from) {
        return from.get(dbFieldName());
    }

    public void setMapper(EntityMapper<?> mapper) {
        this.mapper = mapper;
    }

    public Object getDbValue(String id) {
        if (generationStrategy == IdGeneration.Natural) {
            return id;
        }
        return new ObjectId(id);
    }

    public void natural() {
        generationStrategy = IdGeneration.Natural;
    }

    public void auto() {
        generationStrategy = IdGeneration.Auto;
    }

    private final String name;
    private IdGeneration generationStrategy;
    private static final Logger LOGGER = Logger.getLogger(IdMapper.class);
    private final Method method;
    private EntityMapper<?> mapper;
}
