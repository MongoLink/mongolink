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


import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import org.bson.Document;

import java.util.*;

public class DbObjectDiff {

    public DbObjectDiff(final Document origin) {
        this.origin = origin;
        for (Modifier modifier : Modifier.values()) {
            modifiers.put(modifier, new Document());
        }
    }

    public Document compareWith(Document target) {
        final Document result = new Document();
        generateDiff(target);
        appendChanges(result);
        return result;
    }

    private void generateDiff(Document target) {
        new DocumentVisitor(this, origin).visit(target);
    }

    private void appendChanges(Document result) {
        for (Modifier modifier : Modifier.values()) {
            appendIfNeeded(modifier, modifiers.get(modifier), result);
        }

    }

    private void appendIfNeeded(final Modifier key, final Document entry, final Document result) {
        if (!entry.isEmpty()) {
            result.append(key.key, entry);
        }
    }

    void addPush(Object value) {
        addForModifier(Modifier.PUSH, value);
    }

    void addSet(final Object value) {
        addForModifier(Modifier.SET, value);
    }

    void addPull(final Object value) {
        addForModifier(Modifier.PULL, value);
    }

    void addUnset() {
        addForModifier(Modifier.UNSET, 1);
    }

    public void addPushAll(List<Object> newElements) {
        addForModifier(Modifier.PUSHALL, newElements);
    }

    private void addForModifier(final Modifier key, final Object value) {
        modifiers.get(key).put(makeKey(), value);
    }

    private String makeKey() {
        return Joiner.on(".").join(keys);
    }

    public void pushKey(final String key) {
        keys.push(key);
    }

    public void popKey() {
        keys.pop();
    }

    enum Modifier {
        SET("$set"), PUSH("$push"), UNSET("$unset"), PULL("$pull"), PUSHALL("$pushAll");

        Modifier(String key) {
            this.key = key;
        }

        @Override
        public String toString() {
            return key;
        }

        private final String key;
    }

    private final Stack<String> keys = new Stack<String>();
    private final Document origin;
    private final Map<Modifier, Document> modifiers = Maps.newConcurrentMap();
}
