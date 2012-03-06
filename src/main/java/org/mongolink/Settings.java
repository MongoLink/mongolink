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

import org.mongolink.domain.UpdateStrategies;
import org.mongolink.domain.criteria.CriteriaFactory;


public class Settings {

    public static Settings defaultInstance() {
        Settings settings = new Settings();
        settings.port = 27017;
        settings.host = "127.0.0.1";
        settings.factoryClass = DbFactory.class;
        settings.criteriaFactoryClass = CriteriaFactory.class;
        settings.dbName = "test";
        return settings;
    }

    private Settings() {
    }

    public Settings withHost(String host) {
        this.host = host;
        return this;
    }

    public Settings withPort(int port) {
        this.port = port;
        return this;
    }

    public String getDbName() {
        return dbName;
    }

    public Settings withDbName(String dbName) {
        this.dbName = dbName;
        return this;
    }

    public DbFactory createDbFactory() {
        try {
            DbFactory dbFactory = factoryClass.newInstance();
            dbFactory.setHost(host);
            dbFactory.setPort(port);
            return dbFactory;
        } catch (Exception e) {
            throw new MongoLinkError("Can't create DbFactory", e);
        }
    }

    public Settings withDbFactory(Class<? extends DbFactory> FactoryClass) {
        factoryClass = FactoryClass;
        return this;
    }

    public CriteriaFactory getCriteriaFactory() {
        try {
            return criteriaFactoryClass.newInstance();
        } catch (Exception e) {
            throw new MongoLinkError("Can,t create CriteriaFactory", e);
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


    private Class<? extends DbFactory> factoryClass;
    private String host;
    private int port;
    private String dbName;
    private Class<? extends CriteriaFactory> criteriaFactoryClass;
    private UpdateStrategies updateStrategy = UpdateStrategies.OVERWRITE;
}
