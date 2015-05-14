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

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import org.apache.commons.lang.StringUtils;
import org.mongolink.MongoLinkError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class FieldContainer {

    public FieldContainer(Method method) {
        this(findField(method));
    }

    public FieldContainer(Field field) {
        this.field = field;
        this.field.setAccessible(true);
    }


    private static Field findField(Method method) {
        String fieldName = StringUtils.uncapitalize(method.getName().substring(prefixLength(method), method.getName().length()));
        return Fields.find(method.getDeclaringClass(), fieldName);
    }

    private static int prefixLength(Method method) {
        if (method.getName().startsWith("is")) {
            return 2;
        }
        return 3;
    }

    public String name() {
        return field.getName();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof FieldContainer)) {
            return false;
        }
        FieldContainer other = (FieldContainer) o;
        return Objects.equal(field, other.field);
    }

    public Object value(final Object instance) {
        try {
            return Fields.getValue(instance, field);
        } catch (Exception e) {
            throw new MongoLinkError("Invocation exception : " + toString(), e);
        }
    }

    public void setValueIn(Object value, Object instance) {
        if(value == null) {
            LOGGER.warn("Property value was null : {}", this);
            return;
        }
        Fields.setValue(field, instance, value);
    }

    public Class<?> getReturnType() {
        return field.getType();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(field);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper("Property")
                .add("class", field.getDeclaringClass().getName())
                .add("field", name())
                .toString();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(FieldContainer.class);
    private final Field field;
}
