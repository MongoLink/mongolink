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

package org.mongolink;

import com.mongodb.DB;
import com.mongodb.Mongo;

import java.net.UnknownHostException;

public class DbFactory {

    public DB get(String dbName) {
        initializeMongo();
        return mongo.getDB(dbName);
    }

    private void initializeMongo() {
        if (mongo == null) {
            doInitializeMongo();
        }
    }

    private synchronized void doInitializeMongo() {
        if (mongo == null) {
            try {
                mongo = new Mongo(host, port);
            } catch (UnknownHostException e) {
                throw new MongoLinkError("Can't instanciate mongo", e);
            }
        }
    }

    public void close() {
        if (mongo != null) {
            mongo.close();
        }
    }

    protected void setHost(String host) {
        this.host = host;
    }

    protected void setPort(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

    private Mongo mongo;
    private int port;
    private String host;
}
