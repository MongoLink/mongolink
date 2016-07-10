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

import com.mongodb.MongoClient;
import org.mongolink.domain.criteria.CriteriaFactory;

import java.util.function.Supplier;


public class Settings {

    public static Settings defaultInstance() {
        Settings settings = new Settings();
        settings.factoryClass = DbFactory.class;
        settings.criteriaFactoryClass = CriteriaFactory.class;
        settings.client = Settings::defaultDatabase;
        return settings;
    }

    private static MongoClient defaultDatabase() {
        return new MongoClient();
    }

    private Settings() {
    }

    public DbFactory createDbFactory() {
        try {
            DbFactory dbFactory = factoryClass.newInstance();
            dbFactory.initialize(client.get());
            return dbFactory;
        } catch (Exception e) {
            throw new MongoLinkError("Can't create DbFactory", e);
        }
    }

    public Settings withDbFactory(Class<? extends DbFactory> FactoryClass) {
        factoryClass = FactoryClass;
        return this;
    }

    public Settings withDbName(String dbName) {
        this.dbName = dbName;
        return this;
    }

    public Settings withClient(MongoClient client) {
        this.client = () -> client;
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

    public String getDbName() {
        return dbName;
    }

    private Class<? extends DbFactory> factoryClass;
    private Class<? extends CriteriaFactory> criteriaFactoryClass;
    private UpdateStrategies updateStrategy = UpdateStrategies.OVERWRITE;
    private Supplier<MongoClient> client;
    private String dbName;
}
