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

package fr.bodysplash.mongolink.test.criteria;

import com.mongodb.DBObject;
import fr.bodysplash.mongolink.domain.criteria.RestrictionBetween;

public class FakeRestrictionBetween extends RestrictionBetween implements FakeRestriction {

    public FakeRestrictionBetween(String field, Object start, Object end) {
        super(field, start, end);
        this.start = start;
        this.end = end;
    }

    @Override
    public boolean isSatisfiedBy(DBObject entity) {
        final Comparable value = (Comparable) entity.get(getField());
        return value.compareTo(getDBValue(start)) >= 0 && value.compareTo(getDBValue(end)) < 0;
    }

    private Object start;
    private Object end;
}
