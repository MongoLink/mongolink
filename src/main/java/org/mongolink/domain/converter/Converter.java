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

package fr.bodysplash.mongolink.domain.converter;

import org.joda.time.DateTime;

import java.lang.reflect.Method;

public abstract class Converter {

    private static final PrimitiveConverter PRIMITIVE_CONVERTER = new PrimitiveConverter();

    public static Converter forMethod(Method method) {
        return forType(method.getReturnType());
    }

    public static Converter forType(Class<?> type) {
        if (isEnum(type)) {
            return new EnumConverter(type);
        }
        if (isDateTime(type)) {
            return new DateTimeConverter();
        }
        return PRIMITIVE_CONVERTER;
    }

    private static boolean isDateTime(Class<?> type) {
        return DateTime.class.isAssignableFrom(type);
    }

    private static boolean isEnum(Class<?> type) {
        return type.isEnum() || (type.getSuperclass() != null && type.getSuperclass().isEnum());
    }

    public abstract Object toDbValue(Object value);

    public abstract Object fromDbValue(Object value);
}
