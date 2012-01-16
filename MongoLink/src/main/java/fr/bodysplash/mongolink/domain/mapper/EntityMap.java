package fr.bodysplash.mongolink.domain.mapper;

import com.google.common.collect.Lists;
import org.apache.log4j.Logger;

import java.util.List;

public abstract class EntityMap<T> extends ClassMap<T> {

    protected EntityMap(Class<T> type) {
        super(type);
        mapper = new EntityMapper(type);
    }

    @Override
    protected EntityMapper<T> getMapper() {
        return mapper;
    }

    protected IdMapper id(Object value) {
        LOGGER.debug("Mapping id " + getLastMethod().shortName());
        IdMapper id = new IdMapper(getLastMethod(), IdGeneration.Auto);
        getMapper().setIdMapper(id);
        return id;
    }

    protected <U extends T> void subclass(SubclassMap<U> subclassMap) {
        subclassMap.setParent(this);
        subclasses.add(subclassMap);
    }

    protected void References(Object reference) {
        getMapper().addReference(new ReferenceMapper(getLastMethod()));
    }

    protected void component(Object component) {
        getMapper().addComponent(new PropertyComponentMapper(getLastMethod().getMethod().getReturnType() ,getLastMethod()));
    }

    @Override
    public void buildMapper(MapperContext context) {
        super.buildMapper(context);
        for (SubclassMap<?> subclass : subclasses) {
            subclass.buildMapper(context);
        }
    }

    private EntityMapper<T> mapper;
    private static final Logger LOGGER = Logger.getLogger(EntityMap.class);
    private final List<SubclassMap<? extends T>> subclasses = Lists.newArrayList();
}