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


import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import org.apache.log4j.Logger;
import org.mongolink.domain.converter.Converter;
import org.mongolink.utils.MethodContainer;
import org.mongolink.utils.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;

class CollectionMapper implements Mapper {

    public CollectionMapper(MethodContainer methodContainer) {
        this.name = methodContainer.shortName();
        this.method = methodContainer.getMethod();
    }

    @Override
    public void save(Object instance, DBObject into) {
        try {
            Collection collection = (Collection) method.invoke(instance);
            BasicDBList list = new BasicDBList();
            for (Object child : collection) {
                Object childObject = context().converterFor(child.getClass()).toDbValue(child);
                list.add(childObject);
            }
            into.put(name, list);
        } catch (Exception e) {
            LOGGER.error("Can't saveInto collection " + name, e);
        }
    }

    @Override
    public void populate(Object instance, DBObject from) {
        try {
            Field field = ReflectionUtils.findPrivateField(instance.getClass(), name);
            field.setAccessible(true);
            ParameterizedType elementType = (ParameterizedType) field.getGenericType();
            Converter childMapper = context().converterFor((Class<?>) elementType.getActualTypeArguments()[0]);
            BasicDBList list = (BasicDBList) from.get(name);
            if (list != null) {
                Collection collection = (Collection) field.get(instance);
                for (Object o : list) {
                    collection.add(childMapper.fromDbValue(o));
                }
            }
            field.setAccessible(false);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private MapperContext context() {
        return mapper.getContext();
    }

    public void setMapper(ClassMapper<?> mapper) {
        this.mapper = mapper;
    }

    private final Method method;
    private final String name;
    private ClassMapper<?> mapper;
    private static final Logger LOGGER = Logger.getLogger(CollectionMapper.class);
}
