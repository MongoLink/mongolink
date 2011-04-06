package fr.bodysplash.mongolink;

import com.mongodb.DBAddress;

import java.net.UnknownHostException;

public class DBAddressFactory {

    public DBAddress getLocal(String dbName) {
        try {
            return new DBAddress("127.0.0.1", 27017, dbName);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }

    public DBAddress get(String dbHost, String dbName) {
        try {
            return new DBAddress(dbHost, 27017, dbName);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }

    public DBAddress get(String dbHost, int dbPort, String dbName) {
        try {
            return new DBAddress(dbHost, dbPort, dbName);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }

}
