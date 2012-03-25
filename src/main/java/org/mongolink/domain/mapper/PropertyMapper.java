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


import com.mongodb.DBObject;
import org.apache.log4j.Logger;
import org.mongolink.MongoLinkException;
import org.mongolink.domain.converter.Converter;
import org.mongolink.utils.*;

import java.lang.reflect.*;

class PropertyMapper implements Mapper {

    public PropertyMapper(MethodContainer method) {
        this.name = method.shortName();
        this.method = method.getMethod();
    }

    @Override
    public void save(Object instance, DBObject into) {
        final Object propertyValue = getPropertyValue(instance);
        if (propertyValue != null) {
            into.put(dbFieldName(), converter().toDbValue(propertyValue));
        }
    }

    private Converter converter() {
        return getMapper().getContext().converterFor(method.getReturnType());
    }

    protected Object getPropertyValue(Object element) {
        try {
            return method.invoke(element);
        } catch (Exception e) {
            LOGGER.warn("Can't get value from " + method.getName());
            throw new MongoLinkException(e);
        }
    }

    @Override
    public void populate(Object instance, DBObject from) {
        try {
            Field field = ReflectionUtils.findPrivateField(mapper.getPersistentType(), name);
            field.setAccessible(true);
            field.set(instance, valueFrom(from));
            field.setAccessible(false);
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    protected Object valueFrom(DBObject from) {
        return converter().fromDbValue(from.get(dbFieldName()));
    }

    String dbFieldName() {
        return name;
    }

    public void setMapper(ClassMapper<?> mapper) {
        this.mapper = mapper;
    }

    protected ClassMapper<?> getMapper() {
        return mapper;
    }

    private final String name;
    private static final Logger LOGGER = Logger.getLogger(PropertyMapper.class);
    private final Method method;
    private ClassMapper<?> mapper;
}
