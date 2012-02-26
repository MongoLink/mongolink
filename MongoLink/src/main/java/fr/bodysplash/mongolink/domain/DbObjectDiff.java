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

package fr.bodysplash.mongolink.domain;


import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

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
        for (String key : target.keySet()) {
            diffFor(key, target);
        }
    }

    private void diffFor(String key, DBObject target) {
        final Object field = target.get(key);
        if (isAList(field)) {
            compareList(key, (BasicDBList) field);
        } else {
            compareProperty(key, field);
        }
    }

    private void compareList(final String key, final BasicDBList field) {
        BasicDBList originalList = (BasicDBList) origin.get(key);
        if (originalList.size() == field.size()) {
            return;
        }
        push.put(key, field.get(field.size() - 1));
    }

    private boolean isAList(Object field) {
        return field instanceof BasicDBList;
    }

    private void compareProperty(String key, Object field) {
        if (hasDifference(key, field)) {
            set.append(key, field);
        }
    }

    private boolean hasDifference(final String key, Object field) {
        return !origin.get(key).equals(field);
    }

    private void appendChanges(BasicDBObject result) {
        if (!set.isEmpty()) {
            result.append("$set", set);
        }
        if (!push.isEmpty()) {
            result.append("$push", push);
        }
    }

    private DBObject origin;
    private BasicDBObject set;
    private BasicDBObject push;
}
