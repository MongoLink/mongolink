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

import com.google.common.collect.Maps;

import java.net.UnknownHostException;
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
        if (!collections.containsKey(name)) {
            collections.put(name, new FakeDBCollection(this, name));
        }
        return collections.get(name);
    }

    @Override
    public void cleanCursors(boolean b) throws MongoException {

    }

    @Override
    public CommandResult command(final DBObject cmd, final int options) throws MongoException {
        CommandResult commandResult = new CommandResult(serverAddress());
        commandResult.put("ok", true);
        return commandResult;
    }

    private ServerAddress serverAddress() {
        try {
            return new ServerAddress();
        } catch (UnknownHostException e) {
            return null;
        }
    }

    public final Map<String, FakeDBCollection> collections = Maps.newHashMap();
}