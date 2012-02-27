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


import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import java.util.Stack;

public class DbObjectDiff {

    public DbObjectDiff(final DBObject origin) {
        this.origin = origin;
    }

    public DBObject compareWith(DBObject target) {
        final BasicDBObject result = new BasicDBObject();
        generateDiff(target);
        appendChanges(result);
        return result;
    }

    private void generateDiff(DBObject target) {
        set = new BasicDBObject();
        push = new BasicDBObject();
        new DocumentVisitor(this, origin).visit(target);
    }

    void addPut(Object value) {
        push.put(makeKey(), value);
    }

    void addSet(final Object value) {
        set.append(makeKey(), value);
    }

    private String makeKey() {
        StringBuilder keyBuilder = new StringBuilder();
        for (String s : keys) {
            keyBuilder.append(s);
            keyBuilder.append(".");
        }
        keyBuilder.deleteCharAt(keyBuilder.length() -1);
        return keyBuilder.toString();
    }

    private void appendChanges(BasicDBObject result) {
        if (!set.isEmpty()) {
            result.append("$set", set);
        }
        if (!push.isEmpty()) {
            result.append("$push", push);
        }
    }

    public void pushKey(final String key) {
        keys.push(key);
    }

    public void popKey() {
        keys.pop();
    }

    private Stack<String> keys = new Stack<String>();
    private DBObject origin;
    private BasicDBObject set;
    private BasicDBObject push;
}
