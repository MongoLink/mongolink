package fr.bodysplash.mongolink.mapper;

public abstract class SubclassMap<T> extends AbstractMap<T>{

    private ClassMap<T> parentMap;

    public SubclassMap(Class<T> type) {
        super(type);
    }

    @Override
    protected Mapper<T> createMapper(Class<T> type) {
        return new SubclassMapper<T>(type);
    }

    @Override
    protected SubclassMapper<T> getMapper() {
        return (SubclassMapper<T>) super.getMapper();
    }

    void setParentMap(ClassMap<?> parentMap) {
        getMapper().setParentMapper(parentMap.getMapper());

    }
}
