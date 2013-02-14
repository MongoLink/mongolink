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

package org.mongolink.domain.criteria;

import com.mongodb.DBObject;
import org.mongolink.domain.converter.Converter;

public abstract class Restriction {

    public Restriction(String field) {
        this.field = field;
    }

    public abstract void apply(DBObject query);

    protected String getField() {
        return field;
    }

    protected Object getDBValue(Object value) {
        final Converter converter = Converter.forType(value.getClass());
        return converter.toDbValue(value);
    }

    private final String field;
}
