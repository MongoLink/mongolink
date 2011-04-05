package fr.bodysplash.mongolink.mapper;

public abstract class ComponentMap<T> extends AbstractMap<T> {

    public ComponentMap(Class<T> type) {
        super(type);
    }

    @Override
    protected Mapper<T> createMapper(Class<T> type) {
        return new ComponentMapper(type);
    }
}
