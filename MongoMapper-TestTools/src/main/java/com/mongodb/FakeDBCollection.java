package com.mongodb;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


public class FakeDBCollection extends DBCollection {


    private final List<DBObject> objects = Lists.newArrayList();

    public FakeDBCollection(FakeDB base, String name) {
        super(base, name);
    }

    public List<DBObject> getObjects() {
        return objects;
    }

    @Override
    public WriteResult insert(DBObject[] arr, WriteConcern concern) throws MongoException {
        objects.addAll(Arrays.asList(arr));
        return null;
    }

    @Override
    public WriteResult update(DBObject q, DBObject o, boolean upsert, boolean multi, WriteConcern concern) throws MongoException {
        DBObject old = findOne(q);
        objects.remove(old);
        objects.add(o);
        return null;
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
    Iterator<DBObject> __find(DBObject ref, DBObject fields, int numToSkip, int batchSize, int options) throws MongoException {
        final Object id = ref.get("_id");
        DBObject dbObject = Iterables.find(objects, new Predicate<DBObject>() {
            @Override
            public boolean apply(DBObject input) {
                return input.get("_id").equals(id);
            }
        });
        return Lists.newArrayList(dbObject).iterator();
    }

    @Override
    public void createIndex(DBObject keys, DBObject options) throws MongoException {
    }
}
