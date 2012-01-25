package fr.bodysplash.mongolink.domain.mapper;

public abstract class SubclassMap<T> extends ClassMap<T> {

    public SubclassMap(Class<T> type) {
        super(type);
        mapper = new SubclassMapper<T>(type);
    }

    @Override
    protected SubclassMapper<T> getMapper() {
        return mapper;
    }

    public <U> void setParent(EntityMap<U> parentMap) {
        this.parentMap = parentMap;
    }

    @Override
    public void buildMapper(MapperContext context) {
        map();
        parentMap.getMapper().addSubclass(getMapper());
        getMapper().setContext(context);
    }

    private EntityMap<?> parentMap;
    private SubclassMapper<T> mapper;
}
