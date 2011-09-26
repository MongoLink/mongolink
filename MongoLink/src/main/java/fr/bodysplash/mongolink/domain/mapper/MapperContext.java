package fr.bodysplash.mongolink.domain.mapper;

import com.google.common.collect.Lists;

import java.util.List;

public class MapperContext {


    public <T> Mapper<T> mapperFor(Class<T> aClass) {
        for (Mapper<?> current : mappers) {
            if (current.canMap(aClass)) {
                return (Mapper<T>) current;
            }
        }
        return null;
    }

    void addMapper(Mapper<?> mapper) {
        mapper.setContext(this);
        mappers.add(mapper);
    }

    public List<Mapper<?>> getMappers() {
        return mappers;
    }

    private List<Mapper<?>> mappers = Lists.newArrayList();
}
