package fr.bodysplash.mongolink;

import fr.bodysplash.mongolink.domain.UpdateStrategies;
import fr.bodysplash.mongolink.domain.criteria.CriteriaFactory;

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
