/*
 * MongoLink, Object Document Mapper for Java and MongoDB
 *
 * Copyright (c) 2012, Arpinum or third-party contributors as
 * indicated by the contributors.txt file
 *
 * MongoLink is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * MongoLink is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the Lesser GNU General Public License
 * along with MongoLink.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.mongolink.utils;

import org.mongolink.MongoLinkError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

public final class Fields {
    static void setValue(Field field, Object instance, Object value) {
        boolean visibility = toggleVisibility(field, true);
        try {
            field.set(instance, value);
        } catch (Exception e) {
            LOGGER.error("Error setting property {} with value {} in {}", field.getName(), value, field.getDeclaringClass().getName(), e);
        } finally {
            toggleVisibility(field, visibility);
        }
    }

    static Object getValue(Object instance, Field field) throws IllegalAccessException {
        boolean visibility = toggleVisibility(field, true);
        try {
            return field.get(instance);
        } catch (Exception e) {
            throw new MongoLinkError("Can't get field value", e);
        } finally {
            toggleVisibility(field, visibility);
        }
    }

    private static boolean toggleVisibility(Field field, boolean visibility) {
        boolean result = field.isAccessible();
        field.setAccessible(visibility);
        return result;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(Fields.class);
}
