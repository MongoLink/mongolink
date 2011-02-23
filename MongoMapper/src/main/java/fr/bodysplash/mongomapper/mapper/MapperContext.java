package fr.bodysplash.mongomapper.mapper;

import com.google.common.collect.Maps;

import java.util.Map;

public class MapperContext {

    private Map<Class<?>, Mapper<?>> mappers = Maps.newHashMap();

    public <T> Mapper<T> mapperFor(Class<T> aClass) {
        return (Mapper<T>) mappers.get(aClass);
    }

    void addMapper(Mapper<?> mapper) {
        mapper.setContext(this);
        mappers.put(mapper.getPersistentType(), mapper);
    }
}
