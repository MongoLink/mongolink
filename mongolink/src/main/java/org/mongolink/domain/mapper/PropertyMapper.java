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
import org.mongolink.domain.converter.Converter;
import org.mongolink.utils.FieldContainer;

class PropertyMapper implements Mapper {

    public PropertyMapper(FieldContainer field) {
        this.field = field;
    }

    @Override
    public void save(Object instance, DBObject into) {
        final Object propertyValue = getPropertyValue(instance);
        if (propertyValue != null) {
            into.put(dbFieldName(), converter().toDbValue(propertyValue));
        }
    }

    String dbFieldName() {
        return field.name();
    }

    protected Object getPropertyValue(Object element) {
        return field.value(element);

    }

    private Converter converter() {
        return getMapper().getContext().converterFor(field.getReturnType());
    }

    @Override
    public void populate(Object instance, DBObject from) {
        field.setValueIn(valueFrom(from), instance);
    }

    private Object valueFrom(DBObject from) {
        Object value = null;
        if (from != null) {
            value = from.get(dbFieldName());
        }
        return converter().fromDbValue(value);
    }

    protected ClassMapper<?> getMapper() {
        return mapper;
    }

    public void setMapper(ClassMapper<?> mapper) {
        this.mapper = mapper;
    }

    private ClassMapper<?> mapper;
    private FieldContainer field;
}
