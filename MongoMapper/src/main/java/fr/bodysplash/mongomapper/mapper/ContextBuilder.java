package fr.bodysplash.mongomapper.mapper;

import com.google.common.collect.Lists;

import java.util.List;

public class ContextBuilder {

    private List<Mapping<?>> mappings = Lists.newArrayList();

    public <T> Mapping<T> newMapping(Class<T> clazz) {
        Mapping<T> result = Mapping.createNew(clazz);
        mappings.add(result);
        return result;
    }

    public MapperContext createContext() {
        MapperContext result = new MapperContext();
        for (Mapping<?> mapping : mappings) {
            Mapper<?> mapper = mapping.createMapper();
            mapper.setContext(result);
            result.addMapper(mapper);
        }
        return result;
    }
}
