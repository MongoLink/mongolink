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
import com.google.common.collect.Lists;
import com.mongodb.*;

import javax.net.ssl.SSLSocketFactory;
import java.util.List;

public class DbFactory {

    public DB get(String dbName) {
        initializeMongo(dbName);
        DB db = mongoClient.getDB(dbName);
        return db;
    }

    private boolean isAuthenticationRequired() {
        return !Strings.isNullOrEmpty(user);
    }

    private void initializeMongo(String dbName) {
        if (mongoClient == null) {
            doInitializeMongo(dbName);
        }
    }

    private synchronized void doInitializeMongo(String dbName) {
        if (mongoClient == null) {
            mongoClient = new MongoClient(addresses, buildMongoCredentials(dbName), buildMongoClientOptions());
            mongoClient.setReadPreference(readPreference);
            mongoClient.setWriteConcern(writeConcern);
        }
    }

    private List<MongoCredential> buildMongoCredentials(String dbName) {
        List<MongoCredential> result = Lists.newArrayList();
        if (isAuthenticationRequired()) {
          result.add(MongoCredential.createCredential(user, dbName, password.toCharArray()));
        }
        return result;
    }

    private MongoClientOptions buildMongoClientOptions() {
        MongoClientOptions.Builder builder = new MongoClientOptions.Builder();
        if(this.sslEnabled) {
            builder.socketFactory(SSLSocketFactory.getDefault());
        }

        return builder.build();
    }

    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }

    public List<ServerAddress> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<ServerAddress> adresses) {
        this.addresses = adresses;
    }

    public ReadPreference getReadPreference() {
        return readPreference;
    }

    public void setReadPreference(ReadPreference readPreference) {
        this.readPreference = readPreference;
    }

    public void setAuthInfos(String user, String password) {
        this.user = user;
        this.password = password;
    }

    public WriteConcern getWriteConcern() {
        return writeConcern;
    }

    public void setWriteConcern(WriteConcern writeConcern) {
        this.writeConcern = writeConcern;
    }

    public boolean getSslEnabled() {
        return sslEnabled;
    }

    public void setSslEnabled(boolean sslEnabled) {
        this.sslEnabled = sslEnabled;
    }

    private volatile MongoClient mongoClient;
    private volatile String user;
    private volatile String password;
    private boolean sslEnabled;
    private List<ServerAddress> addresses;
    private ReadPreference readPreference;
    private WriteConcern writeConcern;
}
