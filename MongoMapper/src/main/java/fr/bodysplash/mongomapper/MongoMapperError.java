package fr.bodysplash.mongomapper;

public class MongoMapperError extends RuntimeException {
    public MongoMapperError(String message, Exception cause) {
        super(message, cause);
    }
}
