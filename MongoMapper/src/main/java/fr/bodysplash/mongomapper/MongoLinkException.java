package fr.bodysplash.mongomapper;

public class MongoLinkException extends RuntimeException {
    public MongoLinkException(Exception e) {
        super(e);
    }
}
