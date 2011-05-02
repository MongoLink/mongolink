package fr.bodysplash.mongolink.mapper;

public abstract class SubclassMap<T> extends AbstractMap<T>{

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

    public <U> void setParent(ClassMap<U> parentMap) {
        this.parentMap = parentMap;
    }

    @Override
    public void buildMapper(MapperContext context) {
        map();
        parentMap.getMapper().addSubclass(getMapper());
    }

    private ClassMap<?> parentMap;
}
