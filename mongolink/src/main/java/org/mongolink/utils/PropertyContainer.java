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

package org.mongolink.utils;

import com.google.common.base.Objects;
import org.apache.commons.lang.StringUtils;
import org.mongolink.MongoLinkError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class PropertyContainer {

    public PropertyContainer(Method method) {
        this.method = method;
    }

    public Method getMethod() {
        return method;
    }

    public String shortName() {
        return StringUtils.uncapitalize(method.getName().substring(prefixLength(), method.getName().length()));
    }

    private int prefixLength() {
        if (method.getName().startsWith("is")) {
            return 2;
        }
        return 3;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof PropertyContainer)) {
            return false;
        }
        PropertyContainer other = (PropertyContainer) o;
        return Objects.equal(method, other.method);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(method);
    }

    public Object invoke(final Object instance) {
        try {
            return method.invoke(instance);
        } catch (Exception e) {
            throw new MongoLinkError("Invocation exception : " + toString(), e);
        }
    }

    @Override
    public String toString() {
        return Objects.toStringHelper("Property")
                .add("class", method.getDeclaringClass().getName())
                .add("field", shortName())
                .toString();
    }

    public Class<?> getReturnType() {
        return method.getReturnType();
    }

    public Field findField() {
        try {
            return ReflectionUtils.findPrivateField(method.getDeclaringClass(), shortName());
        } catch (NoSuchFieldException e) {
            throw new MongoLinkError("Error finding field : " + toString());
        }
    }

    public void setValueIn(Object value, Object instance) {
        if(value == null) {
            LOGGER.warn("Property value was null : {}", this);
            return;
        }
        Field field = findField();
        field.setAccessible(true);
        try {
            field.set(instance, value);
        } catch (Exception e) {
            LOGGER.error("Error setting property value : {} ; method : {}", value, this, e);
        }finally {
            field.setAccessible(false);
        }
    }

    private final Method method;
    private static final Logger LOGGER = LoggerFactory.getLogger(PropertyContainer.class);
}
