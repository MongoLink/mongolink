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

package com.mongodb;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.bson.types.ObjectId;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class FakeDBCollection extends DBCollection {

    public FakeDBCollection(FakeDB base, String name) {
        super(base, name);
    }

    public List<DBObject> getObjects() {
        return objects;
    }

    @Override
    public WriteResult insert(DBObject[] arr, WriteConcern concern) throws MongoException {
        if (arr == null) {
            return null;
        }
        return insert(Arrays.asList(arr), concern, null);
    }

    @Override
    public WriteResult insert(List<DBObject> dbObjects, WriteConcern writeConcern, DBEncoder dbEncoder) {
        objects.addAll(dbObjects);
        for (DBObject dbObject : dbObjects) {
            if (dbObject.get("_id") == null) {
                dbObject.put("_id", ObjectId.get());
            }
        }
        return null;
    }

    @Override
    public WriteResult update(DBObject q, DBObject o, boolean upsert, boolean multi, WriteConcern concern) throws MongoException {
        lastUpdate = o;
        DBObject old = findOne(q);
        objects.remove(old);
        objects.add(o);
        return null;
    }

    @Override
    public WriteResult update(DBObject q, DBObject o, boolean upsert, boolean multi, WriteConcern concern, DBEncoder dbEncoder) {
        return update(q,o,upsert,multi,concern);
    }

    @Override
    protected void doapply(DBObject o) {
    }

    @Override
    public WriteResult remove(DBObject o, WriteConcern concern) throws MongoException {
        objects.remove(o);
        return null;
    }

    @Override
    public WriteResult remove(DBObject dbObject, WriteConcern writeConcern, DBEncoder dbEncoder) {
        return remove(dbObject,writeConcern);
    }

    @Override
    Iterator<DBObject> __find(DBObject ref, DBObject fields, int numToSkip, int batchSize, int limit, int options, ReadPreference readPreference, DBDecoder dbDecoder) {
        final Object id = ref.get("_id");
        if (id == null) {
            return returnAll(numToSkip, limit);
        }
        return findById(id);
    }

    @Override
    Iterator<DBObject> __find(DBObject ref, DBObject fields, int numToSkip, int batchSize, int limit, int options, ReadPreference readPreference, DBDecoder dbDecoder, DBEncoder dbEncoder) {
        return __find(ref, fields, numToSkip,batchSize, limit, options, readPreference, dbDecoder);
    }

    private Iterator<DBObject> findById(final Object id) {
        DBObject dbObject;
        try {
            dbObject = Iterables.find(objects, new Predicate<DBObject>() {
                @Override
                public boolean apply(DBObject input) {
                    return input.get("_id").equals(id);
                }
            });
        } catch (Exception e) {
            return Lists.<DBObject>newArrayList().iterator();
        }
        return Lists.newArrayList(dbObject).iterator();
    }

    private Iterator<DBObject> returnAll(int skip, int limit) {
        List<DBObject> result = objects;
        if (skip > 0) {
            result = Lists.newArrayList(Iterables.skip(result, skip));
        }
        if (limit > 0) {
            return Iterables.limit(result, limit).iterator();
        }
        return result.iterator();
    }

    @Override
    public void createIndex(DBObject keys, DBObject options) throws MongoException {
    }

    @Override
    public void createIndex(DBObject dbObject, DBObject dbObject1, DBEncoder dbEncoder) {
    }

    @Override
    public long count() throws MongoException {
        return objects.size();
    }

    public DBObject lastUpdate() {
        return lastUpdate;
    }

    private final List<DBObject> objects = Lists.newArrayList();
    private DBObject lastUpdate;
}