package fr.bodysplash.mongolink.domain;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;

public class CursorParameter {

    public static CursorParameter empty() {
        return new EmptyCursorParameter();
    }

    public static CursorParameter withSkip(int skip) {
        return new CursorParameter().skip(skip);
    }

    public static CursorParameter withLimit(int limit) {
        return new CursorParameter().limit(limit);
    }

    public static CursorParameter withSort(String sortField, int sortOrder) {
        return new CursorParameter().sort(sortField, sortOrder);
    }

    protected CursorParameter() {

    }

    DBCursor apply(DBCursor cursor) {
        cursor = cursor.limit(limit).skip(skip).sort(orderBy);
        return cursor;
    }

    public CursorParameter limit(int limit) {
        this.limit = limit;
        return this;
    }

    public CursorParameter skip(int skip) {
        this.skip = skip;
        return this;
    }

    public CursorParameter sort(String sortField, int sortOrder) {
        orderBy.put(sortField, sortOrder);
        return this;
    }

    public int getLimit() {
        return limit;
    }

    public int getSkip() {
        return skip;
    }

    public BasicDBObject getSort() {
        return orderBy;
    }

    private int skip;
    private int limit;
    private BasicDBObject orderBy = new BasicDBObject();
}
