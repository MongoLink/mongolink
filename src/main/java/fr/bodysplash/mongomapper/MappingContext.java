package fr.bodysplash.mongomapper;

import com.google.common.collect.Maps;

import java.util.Map;

public class MappingContext {

    private Map<Class<?>, Mapper<?>> mappings = Maps.newHashMap();

    public Mapper<?> mapperFor(Class<?> aClass) {
        return mappings.get(aClass);
    }

    void addMapper(Mapper<?> mapper) {
        mappings.put(mapper.getPersistentType(), mapper);
    }
}
