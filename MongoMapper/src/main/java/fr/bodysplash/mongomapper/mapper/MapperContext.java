package fr.bodysplash.mongomapper.mapper;

import com.google.common.collect.Maps;

import java.util.Map;

public class MapperContext {

    private Map<Class<?>, Mapper<?>> mappings = Maps.newHashMap();

    public <T> Mapper<T> mapperFor(Class<T> aClass) {
        return (Mapper<T>) mappings.get(aClass);
    }

    void addMapper(Mapper<?> mapper) {
        mappings.put(mapper.getPersistentType(), mapper);
    }
}
