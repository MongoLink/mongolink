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

import com.mongodb.*;

import java.util.*;


public class DocumentVisitor extends Visitor {

    public DocumentVisitor(final DbObjectDiff dbObjectDiff, final DBObject origin) {
        super(dbObjectDiff, origin);
    }

    @Override
    public void visit(final Object target) {
        final DBObject dbObject = (DBObject) target;
        for (String key : dbObject.keySet()) {
            getDbObjectDiff().pushKey(key);
            visit(key, (DBObject) target);
            getDbObjectDiff().popKey();
        }
    }

    private void visit(String key, DBObject target) {
        final Object field = target.get(key);
        if (isAList(field)) {
            visitList(key, (BasicDBList) field);
        } else {
            visitProperty(key, field);
        }
    }

    private boolean isAList(Object field) {
        return field instanceof BasicDBList;
    }

    private void visitList(final String key, final BasicDBList field) {
        visitList((List) getOrigin().get(key), field);
    }

    private void visitProperty(final String key, final Object field) {
        visitProperty(getOrigin().get(key), field);
    }

    @Override
    protected DBObject getOrigin() {
        return (DBObject) super.getOrigin();
    }

}
