/*
 * MongoLink, Object Document Mapper for Java and MongoDB
 *
 * Copyright (c) 2012, Arpinum or third-party contributors as
 * indicated by the @author tags
 *
 * MongoLink is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MongoLink is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the Lesser GNU General Public License
 * along with MongoLink.  If not, see <http://www.gnu.org/licenses/>. 
 *
 */

package org.mongolink.domain.mapper;

import com.google.common.collect.Lists;
import org.mongolink.domain.converter.Converter;

import java.util.List;

public class MapperContext {

    @SuppressWarnings("unchecked")
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
        if (classMapper != null) {
            return classMapper;
        }
        return Converter.forType(type);
    }

    private final List<ClassMapper<?>> mappers = Lists.newArrayList();
}
