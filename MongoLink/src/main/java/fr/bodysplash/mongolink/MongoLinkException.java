package fr.bodysplash.mongolink;

public class MongoLinkException extends RuntimeException {
    public MongoLinkException(Exception e) {
        super(e);
    }
}
