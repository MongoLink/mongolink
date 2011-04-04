package fr.bodysplash.mongolink;

public class MongoLinkError extends RuntimeException {
    public MongoLinkError(String message, Exception cause) {
        super(message, cause);
    }

    public MongoLinkError(String message) {
        super(message);
    }
}
