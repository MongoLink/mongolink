package fr.bodysplash.mongolink.domain.mapper;


import com.mongodb.*;

public class SubclassMapper<T> extends ClassMapper<T> {

    public static String discriminatorValue(DBObject from) {
        Object discriminator = from.get(DISCRIMINATOR);
        return discriminator == null ? "" : discriminator.toString();
    }

    public SubclassMapper(Class<T> type) {
        super(type);
    }
    
    @Override
    public void save(Object instance, DBObject into) {
        super.save(instance, into);
        into.put(DISCRIMINATOR, discriminator());
    }

    String discriminator() {
        return getPersistentType().getSimpleName();
    }

    void setParentMapper(ClassMapper<?> parentMapper) {
        addMapper(parentMapper);
        this.parentMapper = parentMapper;
    }

    private ClassMapper<?> parentMapper;
    public static final String DISCRIMINATOR = "__discriminator";
}
