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


public abstract class Visitor {
    public Visitor(final DbObjectDiff dbObjectDiff, final Object origin) {
        this.origin = origin;
        this.dbObjectDiff = dbObjectDiff;
    }

    public abstract void visit(Object target);

    protected DbObjectDiff getDbObjectDiff() {
        return dbObjectDiff;
    }

    protected Object getOrigin() {
        return origin;
    }

    protected void visitDocument(final DBObject origin, final Object target) {
        new DocumentVisitor(dbObjectDiff, origin).visit(target);
    }

    public void visitProperty(final Object origin, final Object target) {
        new PropertyVisitor(dbObjectDiff, origin).visit(target);
    }

    protected void visitList(final List origin, final BasicDBList field) {
        new ListVisitor(dbObjectDiff, origin).visit(field);
    }

    private final DbObjectDiff dbObjectDiff;
    private final Object origin;
}
