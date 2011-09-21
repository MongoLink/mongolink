package fr.bodysplash.mongolink;

import com.mongodb.*;

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
