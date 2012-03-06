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

import com.google.common.collect.Lists;
import org.apache.log4j.Logger;

import java.util.List;

public abstract class EntityMap<T> extends ClassMap<T> {

    protected EntityMap(Class<T> type) {
        super(type);
        mapper = new EntityMapper(type);
    }

    @Override
    protected EntityMapper<T> getMapper() {
        return mapper;
    }

    protected IdMapper id(Object value) {
        LOGGER.debug("Mapping id " + getLastMethod().shortName());
        IdMapper id = new IdMapper(getLastMethod(), IdGeneration.Auto);
        getMapper().setIdMapper(id);
        return id;
    }

    protected <U extends T> void subclass(SubclassMap<U> subclassMap) {
        subclassMap.setParent(this);
        subclasses.add(subclassMap);
    }

    protected void References(Object reference) {
        getMapper().addReference(new ReferenceMapper(getLastMethod()));
    }

    @Override
    public void buildMapper(MapperContext context) {
        super.buildMapper(context);
        for (SubclassMap<?> subclass : subclasses) {
            subclass.buildMapper(context);
        }
    }

    private EntityMapper<T> mapper;
    private static final Logger LOGGER = Logger.getLogger(EntityMap.class);
    private final List<SubclassMap<? extends T>> subclasses = Lists.newArrayList();
}