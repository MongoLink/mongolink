package fr.bodysplash.mongolink.domain.mapper;

import com.google.common.collect.Lists;
import fr.bodysplash.mongolink.domain.converter.Converter;

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

    public Converter converterFor(Class<?> type) {
        final ClassMapper<?> classMapper = mapperFor(type);
        if(classMapper != null) {
            return classMapper;
        }
        return Converter.forType(type);
    }

    private List<ClassMapper<?>> mappers = Lists.newArrayList();
}
