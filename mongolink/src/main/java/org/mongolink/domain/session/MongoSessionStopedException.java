package org.mongolink.domain.session;

import org.mongolink.MongoLinkError;

public class MongoSessionStopedException extends MongoLinkError {

    public MongoSessionStopedException() {
        super("Trying to start an already stoped session.");
    }
}
