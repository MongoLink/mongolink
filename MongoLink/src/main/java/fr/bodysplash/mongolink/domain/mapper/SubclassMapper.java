package fr.bodysplash.mongolink.domain.mapper;


import com.mongodb.*;

public class SubclassMapper<T> extends ClassMapper<T> {

    public static String discriminatorValue(DBObject from) {
        Object discriminator = from.get("__discriminator");
        return discriminator == null ? "" : discriminator.toString();
    }

    public SubclassMapper(Class<T> type) {
        super(type);
    }

    @Override
    protected void doPopulate(T instance, DBObject from) {
        parentMapper.populate(instance, from);
    }

    @Override
    protected void doSave(Object element, DBObject object) {
        parentMapper.save(element, object);
        object.put("__discriminator", discriminator());
    }

    String discriminator() {
        return getPersistentType().getSimpleName();
    }

    void setParentMapper(ClassMapper<?> parentMapper) {
        this.parentMapper = parentMapper;
    }

    private ClassMapper<?> parentMapper;
}
