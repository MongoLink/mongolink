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

import com.google.common.base.Strings;
import com.mongodb.*;

import java.net.UnknownHostException;

public class DbFactory {

    public DB get(String dbName) {
        initializeMongo();
        DB db = mongoClient.getDB(dbName);
        if(isAuthenticationRequired()) {
            db.authenticate(user, password.toCharArray());
        }
        return db;
    }

    private boolean isAuthenticationRequired() {
        return !Strings.isNullOrEmpty(user);
    }

    private void initializeMongo() {
        if (mongoClient == null) {
            doInitializeMongo();
        }
    }

    private synchronized void doInitializeMongo() {
        if (mongoClient == null) {
            try {
                mongoClient = new MongoClient(host, port);
            } catch (UnknownHostException e) {
                throw new MongoLinkError("Can't instanciate mongo", e);
            }
        }
    }

    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
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

    public void setAuthInfos(String user, String password) {
        this.user = user;
        this.password = password;
    }

    private volatile MongoClient mongoClient;
    private volatile int port;
    private volatile String host;
    private volatile String user;
    private volatile String password;
}
