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

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.mongolink.utils.FieldContainer;
import org.mongolink.utils.Fields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Map;

public class MapMapper implements Mapper {

    public MapMapper(FieldContainer fieldContainer) {
        this.fieldContainer = fieldContainer;
    }

    @Override
    public void save(final Object instance, final DBObject into) {
        try {
            Map map = value(instance);
            into.put(name(), new BasicDBObject(map));
        } catch (Exception e) {
            LOGGER.error("Can't save map from {}", name(), e);
        }
    }

    private Map value(Object instance) {
        return (Map) fieldContainer.value(instance);
    }

    @Override
    public void populate(final Object instance, final DBObject from) {
        try {
            Field field = Fields.find(instance.getClass(), name());
            field.setAccessible(true);
            Map dbMap = (Map) from.get(name());
            if (dbMap != null) {
                Map map = (Map) field.get(instance);
                map.putAll(dbMap);
            }
            field.setAccessible(false);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private String name() {
        return fieldContainer.name();
    }


    private static final Logger LOGGER = LoggerFactory.getLogger(CollectionMapper.class);
    private final FieldContainer fieldContainer;
}
