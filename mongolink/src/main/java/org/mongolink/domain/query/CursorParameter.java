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

package org.mongolink.domain.query;

import com.mongodb.*;

public class CursorParameter {

    public static CursorParameter empty() {
        return new EmptyCursorParameter();
    }

    public static CursorParameter withSkip(int skip) {
        return new CursorParameter().skip(skip);
    }

    public static CursorParameter withLimit(int limit) {
        return new CursorParameter().limit(limit);
    }

    public static CursorParameter withSort(String sortField, int sortOrder) {
        return new CursorParameter().sort(sortField, sortOrder);
    }

    protected CursorParameter() {

    }

    DBCursor apply(DBCursor cursor) {
        cursor = cursor.limit(limit).skip(skip).sort(orderBy);
        return cursor;
    }

    public CursorParameter limit(int limit) {
        this.limit = limit;
        return this;
    }

    public CursorParameter skip(int skip) {
        this.skip = skip;
        return this;
    }

    public CursorParameter sort(String sortField, int sortOrder) {
        orderBy.put(sortField, sortOrder);
        return this;
    }

    public int getLimit() {
        return limit;
    }

    public int getSkip() {
        return skip;
    }

    public BasicDBObject getSort() {
        return orderBy;
    }

    private int skip;
    private int limit;
    private final BasicDBObject orderBy = new BasicDBObject();
}
