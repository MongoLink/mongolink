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

import com.google.common.collect.Sets;

import java.net.UnknownHostException;
import java.util.Set;

import static org.mockito.Mockito.*;


public class FakeDB extends DB {

    public FakeDB() {
        super(mock(Mongo.class), "test");
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
        return mock(DBCollection.class);
    }

    @Override
    public void cleanCursors(boolean b) throws MongoException {

    }

    @Override
    public CommandResult command(final DBObject cmd, final int options) throws MongoException {
        return okResult();
    }

    @Override
    public Set<String> getCollectionNames() {
        return Sets.newConcurrentHashSet();
    }

    @Override
    public boolean isAuthenticated() {
        return credentials != null;
    }

    @Override
    CommandResult doAuthenticate(MongoCredential credentials) {
        this.credentials = credentials;
        return okResult();
    }

    private CommandResult okResult() {
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

    private MongoCredential credentials;

}