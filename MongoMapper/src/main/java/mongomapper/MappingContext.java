package mongomapper;

import com.google.common.collect.Maps;

import java.util.Map;

public class MappingContext {

    private Map<Class<?>, Mapper<?>> mappings = Maps.newHashMap();

    public <T> Mapper<T> mapperFor(Class<T> aClass) {
        return (Mapper<T>) mappings.get(aClass);
    }

    void addMapper(Mapper<?> mapper) {
        mappings.put(mapper.getPersistentType(), mapper);
    }
}
