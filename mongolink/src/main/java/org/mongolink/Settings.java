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
import org.mongolink.domain.criteria.CriteriaFactory;

import java.net.UnknownHostException;
import java.util.List;


public class Settings {

    public static Settings defaultInstance() {
        Settings settings = new Settings();
        settings.addresses = Lists.newArrayList(serverAddress("127.0.0.1", 27017));
        settings.factoryClass = DbFactory.class;
        settings.criteriaFactoryClass = CriteriaFactory.class;
        settings.dbName = "test";
        return settings;
    }

    private Settings() {
    }

    public DbFactory createDbFactory() {
        try {
            DbFactory dbFactory = factoryClass.newInstance();
            dbFactory.setAddresses(addresses);
            dbFactory.setReadPreference(readPreference);
            dbFactory.setWriteConcern(writeConcern);
            if (authenticationRequired()) {
                dbFactory.setAuthInfos(user, password);
            }
            return dbFactory;
        } catch (Exception e) {
            throw new MongoLinkError("Can't create DbFactory", e);
        }
    }

    public boolean authenticationRequired() {
        return !Strings.isNullOrEmpty(user);
    }

    public Settings withHost(String host) {
        addresses.set(0, serverAddress(host, addresses.get(0).getPort()));
        return this;
    }

    public Settings withPort(int port) {
        addresses.set(0, serverAddress(addresses.get(0).getHost(), port));
        return this;
    }

    public Settings withAuthentication(String user, String password) {
        this.user = user;
        this.password = password;
        return this;
    }

    public String getDbName() {
        return dbName;
    }

    public Settings withDbName(String dbName) {
        this.dbName = dbName;
        return this;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public Settings withDbFactory(Class<? extends DbFactory> FactoryClass) {
        factoryClass = FactoryClass;
        return this;
    }

    public CriteriaFactory getCriteriaFactory() {
        try {
            return criteriaFactoryClass.newInstance();
        } catch (Exception e) {
            throw new MongoLinkError("Can't create CriteriaFactory", e);
        }
    }

    public Settings withCriteriaFactory(Class<? extends CriteriaFactory> criteriaFactoryClass) {
        this.criteriaFactoryClass = criteriaFactoryClass;
        return this;
    }

    public UpdateStrategies getUpdateStrategy() {
        return updateStrategy;
    }

    public Settings withDefaultUpdateStrategy(UpdateStrategies strategy) {
        updateStrategy = strategy;
        return this;
    }

    public Settings withAddresses(List<ServerAddress> serverAddresses) {
        addresses = serverAddresses;
        return this;
    }

    public Settings withReadPreference(ReadPreference readPreference) {
        this.readPreference = readPreference;
        return this;
    }

    public ReadPreference getReadPreference() {
        return readPreference;
    }

    private static ServerAddress serverAddress(String host, int port) {
        try {
            return new ServerAddress(host, port);
        } catch (UnknownHostException e) {
            throw new MongoLinkError(e);
        }
    }

    public Settings withWriteConcern(WriteConcern writeConcern) {
        this.writeConcern = writeConcern;
        return this;
    }

    private Class<? extends DbFactory> factoryClass;
    private String user;
    private String password;
    private String dbName;
    private List<ServerAddress> addresses;
    private Class<? extends CriteriaFactory> criteriaFactoryClass;
    private UpdateStrategies updateStrategy = UpdateStrategies.OVERWRITE;
    private ReadPreference readPreference = ReadPreference.primary();
    private WriteConcern writeConcern = WriteConcern.NORMAL;
}
