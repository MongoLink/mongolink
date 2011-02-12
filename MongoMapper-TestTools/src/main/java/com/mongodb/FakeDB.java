package com.mongodb;

import com.google.common.collect.Maps;

import java.util.Map;

import static org.mockito.Mockito.mock;


public class FakeDB extends DB {
    
    public FakeDB() {
        super(mock(Mongo.class), null);
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
