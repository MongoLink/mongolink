package fr.bodysplash.mongolink.domain.updateStategy;

public abstract class Visitor {
    public Visitor(final DbObjectDiff dbObjectDiff, String key, final Object origin) {
        this.origin = origin;
        this.key = key;
        this.dbObjectDiff = dbObjectDiff;
    }

    public abstract void visit(Object target);

    protected DbObjectDiff getDbObjectDiff() {
        return dbObjectDiff;
    }

    protected String getKey() {
        return key;
    }

    protected Object getOrigin() {
        return origin;
    }

    private DbObjectDiff dbObjectDiff;
    private String key;
    private Object origin;
}
