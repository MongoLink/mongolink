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

import com.mongodb.*;

public class RestrictionBetween extends Restriction {

    public RestrictionBetween(String field, Object start, Object end) {
        super(field);
        this.start = start;
        this.end = end;
    }

    @Override
    public void apply(DBObject query) {
        final BasicDBObject object = new BasicDBObject();
        object.put("$gte", getDBValue(start));
        object.put("$lt", getDBValue(end));
        query.put(getField(), object);
    }

    private final Object start;
    private final Object end;
}
