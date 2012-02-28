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


import com.google.common.collect.Maps;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import java.util.Map;
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
        for (Modifier modifier : Modifier.values()) {
            modifiers.put(modifier, new BasicDBObject());
        }
        new DocumentVisitor(this, origin).visit(target);
    }

    void addPush(Object value) {
        addForModifier(Modifier.PUSH, value);
    }

    void addSet(final Object value) {
        addForModifier(Modifier.SET, value);
    }

    public void appPull(final Object value) {
        addForModifier(Modifier.PULL, value);
    }

    private void addForModifier(final Modifier key, final Object value) {
        modifiers.get(key).put(makeKey(), value);
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
        for (Map.Entry<Modifier, BasicDBObject> entry : modifiers.entrySet()) {
            appendIfNeeded(entry, result);
        }
    }

    private void appendIfNeeded(final Map.Entry<Modifier, BasicDBObject> entry, final BasicDBObject result) {
        if(!entry.getValue().isEmpty()) {
            result.append(entry.getKey().key, entry.getValue());
        }
    }

    public void pushKey(final String key) {
        keys.push(key);
    }

    public void popKey() {
        keys.pop();
    }

    private enum Modifier {
        SET("$set"), PUSH("$push"), PULL("$pull");
        Modifier(String key) {
            this.key = key;
        }

        private String key;
    }
    
    private Stack<String> keys = new Stack<String>();
    private DBObject origin;
    private Map<Modifier, BasicDBObject> modifiers = Maps.newConcurrentMap();
}
