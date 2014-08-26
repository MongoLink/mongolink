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

package org.mongolink.domain.converter;

import org.joda.money.Money;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.lang.reflect.Method;

public abstract class Converter {

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
        if (isLocalDate(type)) {
            return new LocalDateConverter();
        }
        if (isMoney(type)) {
            return new MoneyConverter();
        }
        if(isJava8LocalDate(type)) {
            return new Java8LocalDateConverter();
        }
        if(isJava8LocalDateTime(type)) {
            return new Java8LocalDateTimeConverter();
        }
        return PRIMITIVE_CONVERTER;
    }

    private static boolean isMoney(Class<?> type) {
        return Money.class.isAssignableFrom(type);
    }

    private static boolean isDateTime(Class<?> type) {
        return DateTime.class.isAssignableFrom(type);
    }

    private static boolean isLocalDate(Class<?> type) {
        return LocalDate.class.isAssignableFrom(type);
    }

    private static boolean isJava8LocalDate(Class<?> type) {
        return java.time.LocalDate.class.isAssignableFrom(type);
    }

    private static boolean isJava8LocalDateTime(Class<?> type) {
        return java.time.LocalDateTime.class.isAssignableFrom(type);
    }

    private static boolean isEnum(Class<?> type) {
        return type.isEnum() || (type.getSuperclass() != null && type.getSuperclass().isEnum());
    }

    public abstract Object toDbValue(Object value);

    public Object fromDbValue(Object value) {
        if(value == null) {
            return nullValue();
        }
        return fromDbValueNotNull(value);
    }

    protected abstract Object fromDbValueNotNull(Object value);

    protected Object nullValue() {
        return null;
    }

    private static final PrimitiveConverter PRIMITIVE_CONVERTER = new PrimitiveConverter();
}
