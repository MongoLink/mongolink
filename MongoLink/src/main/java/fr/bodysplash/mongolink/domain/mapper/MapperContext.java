package fr.bodysplash.mongolink.domain.mapper;

import com.google.common.collect.Lists;

import java.util.List;

public class MapperContext {


    public <T> ClassMapper<T> mapperFor(Class<T> aClass) {
        for (ClassMapper<?> current : mappers) {
            if (current.canMap(aClass)) {
                return (ClassMapper<T>) current;
            }
        }
        return null;
    }

    void addMapper(ClassMapper<?> mapper) {
        mapper.setContext(this);
        mappers.add(mapper);
    }

    public List<ClassMapper<?>> getMappers() {
        return mappers;
    }

    private List<ClassMapper<?>> mappers = Lists.newArrayList();
}
