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

package fr.bodysplash.mongolink.domain.mapper;


import com.mongodb.DBObject;
import fr.bodysplash.mongolink.MongoLinkError;
import fr.bodysplash.mongolink.utils.MethodContainer;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class IdMapper implements Mapper {

    public IdMapper(MethodContainer methodContainer, IdGeneration generationStrategy) {
        this.method = methodContainer.getMethod();
        this.name = methodContainer.shortName();
        this.generationStrategy = generationStrategy;
    }

    @Override
    public void save(Object instance, DBObject into) {
        try {
            into.put(dbFieldName(), getIdValue(instance));
        } catch (Exception e) {
            LOGGER.error("Can't saveInto property " + name, e);
        }
    }

    String dbFieldName() {
        return "_id";
    }

    @Override
    public void populate(Object instance, DBObject from) {
        try {
            Field field = mapper.getPersistentType().getDeclaredField(name);
            field.setAccessible(true);
            Object value = getIdValue(from);
            field.set(instance, value.toString());
            field.setAccessible(false);
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    protected Object getIdValue(Object element) {
        try {
            Object keyValue = method.invoke(element);
            if (generationStrategy == IdGeneration.Auto && keyValue != null) {
                return new ObjectId(keyValue.toString());
            }
            return keyValue;
        } catch (Exception e) {
            throw new MongoLinkError("Can't get id value", e);
        }
    }

    protected Object getIdValue(DBObject from) {
        return from.get(dbFieldName());
    }

    public void setMapper(EntityMapper<?> mapper) {
        this.mapper = mapper;
    }

    public Object convertToDbValue(String id) {
        if (generationStrategy == IdGeneration.Natural) {
            return id;
        }
        return new ObjectId(id);
    }

    public void natural() {
        generationStrategy = IdGeneration.Natural;
    }

    public void auto() {
        generationStrategy = IdGeneration.Auto;
    }

    private final String name;
    private IdGeneration generationStrategy;
    private static final Logger LOGGER = Logger.getLogger(IdMapper.class);
    private final Method method;
    private EntityMapper<?> mapper;
}
