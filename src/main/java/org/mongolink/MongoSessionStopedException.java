package org.mongolink;

public class MongoSessionStopedException extends MongoLinkError{

    public MongoSessionStopedException() {
        super("Trying to start an already stoped session.");
    }
}
