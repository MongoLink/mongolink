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

import org.mongolink.utils.FieldContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.*;

public abstract class AggregateMap<T> extends ClassMap<T> {

    @SuppressWarnings("unchecked")
    public AggregateMap() {
        super();
        mapper = new AggregateMapper(persistentType());
    }

    @Override
    protected AggregateMapper<T> getMapper() {
        return mapper;
    }

    @Deprecated
    protected IdMapper id(Object value) {
        return id().onProperty(value);
    }

    protected IdMap id() {
        return new IdMap();
    }

    protected void setCapped(int cappedSize, int cappedMax) {
        getMapper().setCapped(cappedSize, cappedMax);
    }

    public class IdMap {

        @SuppressWarnings("UnusedParameters")
        @Deprecated
        public IdMapper onProperty(Object value) {
            return addMapper(getLastMethod());
        }

        public IdMapper onProperty(Consumer<T> consumer) {
            consumer.accept(element());
            return addMapper(getLastMethod());
        }

        public IdMapper onField(String fieldName) {
            return addMapper(fieldContainer(fieldName));
        }

        private IdMapper addMapper(FieldContainer field) {
            LOGGER.debug("Mapping id : {}", field);
            IdMapper id = new IdMapper(field, IdGeneration.Auto);
            getMapper().setIdMapper(id);
            return id;
        }
    }

    private final AggregateMapper<T> mapper;
    private static final Logger LOGGER = LoggerFactory.getLogger(ClassMap.class);
}