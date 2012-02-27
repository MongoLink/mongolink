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

package fr.bodysplash.mongolink.domain.updateStategy;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;

/**
 * @author jbdusseaut
 */
public class DocumentVisitor extends Visitor{

    public DocumentVisitor(final DbObjectDiff dbObjectDiff, final DBObject origin) {
        super(dbObjectDiff, "", origin);
    }

    @Override
    public void visit(final Object target) {
        final DBObject dbObject = (DBObject) target;
        for (String key : dbObject.keySet()) {
            visit(key, (DBObject) target);
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

    private void visitList(final String key, final BasicDBList field) {
        new ListVisitor(getDbObjectDiff(), key, (BasicDBList) getOrigin().get(key)).visit((BasicDBList) field);
    }

    private void visitProperty(final String key, final Object field) {
        new PropertyVisitor(getDbObjectDiff(), key, getOrigin().get(key)).visit(field);

    }

    @Override
    protected DBObject getOrigin() {
        return (DBObject) super.getOrigin();
    }

    private boolean isAList(Object field) {
        return field instanceof BasicDBList;
    }

}