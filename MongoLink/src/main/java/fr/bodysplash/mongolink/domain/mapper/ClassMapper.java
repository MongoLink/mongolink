package fr.bodysplash.mongolink.domain.mapper;

import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import net.sf.cglib.core.ReflectUtils;
import org.apache.log4j.Logger;

import java.util.List;

@SuppressWarnings("unchecked")
public abstract class ClassMapper<T> implements Mapper {

    public ClassMapper(Class<T> persistentType) {
        this.persistentType = persistentType;
    }

    public Class<T> getPersistentType() {
        return persistentType;
    }

    void setContext(MapperContext context) {
        this.context = context;
    }

    void addCollection(CollectionMapper collection) {
        collection.setMapper(this);
        mappers.add(collection);
    }

    public void addProperty(PropertyMapper property) {
        property.setMapper(this);
        mappers.add(property);
    }

    public T toInstance(DBObject from) {
        T instance = makeInstance();
        populate(instance, from);
        return instance;
    }

    protected T makeInstance() {
        return (T) ReflectUtils.newInstance(persistentType);
    }

    @Override
    public void populate(Object instance, DBObject from) {
        for (Mapper mapper : mappers) {
            mapper.populate(instance, from);
        }
        doPopulate((T) instance, from);
    }
    
    protected abstract void doPopulate(T instance, DBObject from);

    public DBObject toDBObject(Object element) {
        BasicDBObject object = new BasicDBObject();
        save(element, object);
        return object;
    }

    @Override
    public void save(Object instance, DBObject into) {
        for (Mapper mapper : mappers) {
            mapper.save(instance, into);
        }
        doSave(instance, into);
    }

    protected abstract void doSave(Object element, DBObject object);

    public MapperContext getContext() {
        return context;
    }

    public boolean canMap(Class<?> aClass) {
        return persistentType.isAssignableFrom(aClass);
    }

    public boolean isCapped() {
        return capped;
    }

    public void setCapped(boolean capped, int cappedSize, int cappedMax) {
        this.capped = capped;
        this.cappedSize = cappedSize;
        this.cappedMax = cappedMax;
    }

    public int getCappedSize() {
        return cappedSize;
    }

    public int getCappedMax() {
        return cappedMax;
    }

    private int cappedSize;
    private int cappedMax;
    private boolean capped = false;
    private static final Logger LOGGER = Logger.getLogger(EntityMapper.class);
    protected final Class<T> persistentType;
    private final List<Mapper> mappers = Lists.newArrayList();
    private MapperContext context;
}
