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
import com.mongodb.client.MongoDatabase;
import org.mongolink.domain.criteria.CriteriaFactory;

import java.util.function.Supplier;


public class Settings {

    public static Settings defaultInstance() {
        Settings settings = new Settings();
        settings.criteriaFactoryClass = CriteriaFactory.class;
        settings.databaseBuilder = Settings::defaultDatabase;
        return settings;
    }

    private static MongoDatabase defaultDatabase() {
        return new MongoClient().getDatabase("test");
    }

    private Settings() {
    }

    public Settings withDatabase(MongoDatabase database) {
        this.databaseBuilder = () -> database;
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

    public MongoDatabase buildDatabase() {
        return databaseBuilder.get();
    }

    private Class<? extends CriteriaFactory> criteriaFactoryClass;
    private UpdateStrategies updateStrategy = UpdateStrategies.OVERWRITE;
    private Supplier<MongoDatabase> databaseBuilder;

}
