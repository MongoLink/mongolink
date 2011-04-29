package fr.bodysplash.mongolink.mapper;

import com.google.common.collect.Lists;
import org.apache.log4j.Logger;

import java.util.List;

public abstract class ClassMap<T> extends AbstractMap<T> {

    protected ClassMap(Class<T> type) {
        super(type);
    }

    @Override
    protected Mapper<T> createMapper(Class<T> type) {
        return new EntityMapper(type);
    }

    @Override
    protected EntityMapper<T> getMapper() {
        return (EntityMapper<T>) super.getMapper();
    }

    protected IdMapper id(Object value) {
        LOGGER.debug("Mapping id " + getLastMethod().shortName());
        IdMapper id = new IdMapper(getLastMethod(), IdGeneration.Auto);
        getMapper().setId(id);
        return id;
    }

    protected <U extends T> void subclass(SubclassMap<U> subclassMap) {
        subclasses.add(subclassMap);
        subclassMap.setParentMap(this);
    }

    @Override
    public void buildMapper(MapperContext context) {
        super.buildMapper(context);
        for (SubclassMap<?> subclass : subclasses) {
            subclass.buildMapper(context);
        }
    }

    private static final Logger LOGGER = Logger.getLogger(ClassMap.class);
    private final List<SubclassMap<? extends T>> subclasses = Lists.newArrayList();
}
