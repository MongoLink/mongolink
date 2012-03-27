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


import com.google.common.collect.Maps;
import com.mongodb.DBObject;

import java.util.Map;

@SuppressWarnings("unchecked")
public class EntityMapper<T> extends ClassMapper<T> {

    public EntityMapper(Class<T> persistentType) {
        super(persistentType);
    }

    public void populateId(Object element, DBObject dbObject) {
        idMapper.populate(element, dbObject);
    }

    void setIdMapper(IdMapper idMapper) {
        idMapper.setMapper(this);
        addMapper(idMapper);
        this.idMapper = idMapper;
    }

    public Object getDbId(Object id) {
        return idMapper.convertToDbValue(id);
    }

    public Object getId(Object entity) {
        return idMapper.getIdValue(entity);
    }

    public Object getId(DBObject dbo) {
        return idMapper.getIdValue(dbo);
    }

    <U> void addSubclass(SubclassMapper<U> mapper) {
        mapper.setParentMapper(this);
        subclasses.put(mapper.discriminator(), mapper);
    }

    void addReference(ReferenceMapper mapper) {
        //To change body of created methods use File | Settings | File Templates.
    }

    @Override
    public T toInstance(DBObject from) {
        String discriminator = SubclassMapper.discriminatorValue(from);
        if (subclasses.get(discriminator) != null) {
            return (T) subclasses.get(discriminator).toInstance(from);
        }
        return super.toInstance(from);
    }

    @Override
    public DBObject toDBObject(Object element) {
        if (isSubclass(element)) {
            return subclassMapperFor(element).toDBObject(element);
        }
        return super.toDBObject(element);
    }

    private boolean isSubclass(Object element) {
        return subclassMapperFor(element) != null;
    }

    private SubclassMapper<?> subclassMapperFor(Object element) {
        for (SubclassMapper<?> subclassMapper : subclasses.values()) {
            if (subclassMapper.getPersistentType().isAssignableFrom(element.getClass())) {
                return subclassMapper;
            }
        }
        return null;
    }

    public String collectionName() {
        return getPersistentType().getSimpleName().toLowerCase();
    }

    private IdMapper idMapper;
    private final Map<String, SubclassMapper<?>> subclasses = Maps.newHashMap();
}
