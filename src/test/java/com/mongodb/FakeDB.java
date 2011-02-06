package com.mongodb;

import com.google.common.collect.Maps;

import java.util.Map;


public class FakeDB extends DB {
    
    public FakeDB(Mongo mongo, String name) {
        super(mongo, name);
    }

    @Override
    public void requestStart() {

    }

    @Override
    public void requestDone() {

    }

    @Override
    public void requestEnsureConnection() {

    }

    @Override
    public DBCollection doGetCollection(String name) {
        return collections.get(name);
    }

    public Map<String, FakeDBCollection> collections = Maps.newHashMap();
}
