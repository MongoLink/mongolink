package fr.bodysplash.mongolink.domain.mapper;

import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import fr.bodysplash.mongolink.domain.converter.Converter;
import net.sf.cglib.core.ReflectUtils;
import org.apache.log4j.Logger;

import java.util.List;

@SuppressWarnings("unchecked")
public abstract class ClassMapper<T> extends Converter implements Mapper {

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
        addMapper(collection);
    }

    public void addProperty(PropertyMapper property) {
        property.setMapper(this);
        addMapper(property);
    }

    protected void addMapper(Mapper property) {
        mappers.add(property);
    }

    @Override
    public Object fromDbValue(Object value) {
        return toInstance((DBObject) value);
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
    }

    @Override
    public Object toDbValue(Object value) {
        return toDBObject(value);
    }

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
    }

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
    protected final Class<T> persistentType;
    private final List<Mapper> mappers = Lists.newArrayList();
    private MapperContext context;
    private static final Logger LOGGER = Logger.getLogger(EntityMapper.class);
}
