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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AggregateMap<T> extends ClassMap<T> {

    @SuppressWarnings("unchecked")
    protected AggregateMap(Class<T> type) {
        super(type);
        mapper = new AggregateMapper(type);
    }

    @Override
    protected AggregateMapper<T> getMapper() {
        return mapper;
    }

    protected IdMapper id(Object value) {
        LOGGER.debug("Mapping id {}", getLastMethod().shortName());
        IdMapper id = new IdMapper(getLastMethod(), IdGeneration.Auto);
        getMapper().setIdMapper(id);
        return id;
    }

    protected void setCapped(int cappedSize, int cappedMax) {
        getMapper().setCapped(cappedSize, cappedMax);
    }

    private final AggregateMapper<T> mapper;
    private static final Logger LOGGER = LoggerFactory.getLogger(AggregateMap.class);
}