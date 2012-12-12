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


import com.mongodb.DBObject;

@SuppressWarnings("unchecked")
public class AggregateMapper<T> extends ClassMapper<T> {

    public AggregateMapper(Class<T> persistentType) {
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

    public String collectionName() {
        return getPersistentType().getSimpleName().toLowerCase();
    }

    @Override
    public boolean isCapped() {
        return capped.isCapped();
    }

    public void setCapped(int cappedSize, int cappedMax) {
        this.capped = new Capped().withSize(cappedSize).withMax(cappedMax);
    }

    public Capped getCapped() {
        return capped;
    }

    private Capped capped = new NotCapped();

    private IdMapper idMapper;
}
