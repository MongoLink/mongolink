package fr.bodysplash.mongolink.domain.mapper;

public abstract class ComponentMap<T> extends ClassMap<T> {

    public ComponentMap(Class<T> type) {
        super(type);
        mapper = new ComponentMapper(type);
    }

    @Override
    protected ClassMapper<T> getMapper() {
        return mapper;
    }

    private ComponentMapper<T> mapper;
}
