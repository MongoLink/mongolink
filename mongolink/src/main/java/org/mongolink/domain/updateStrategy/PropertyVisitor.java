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

package org.mongolink.domain.updateStrategy;

import com.mongodb.DBObject;


public class PropertyVisitor extends Visitor {

    public PropertyVisitor(final DbObjectDiff dbObjectDiff, final Object origin) {
        super(dbObjectDiff, origin);
    }

    @Override
    public void visit(final Object field) {
        if (isADocument(field)) {
            visitDocument((DBObject) getOrigin(), field);
        } else if (hasDifference(field)) {
            getDbObjectDiff().addSet(field);
        }
    }

    private boolean isADocument(final Object field) {
        return DBObject.class.isAssignableFrom(field.getClass());
    }

    private boolean hasDifference(Object field) {
        return getOrigin() == null || !getOrigin().equals(field);
    }

}
