package fr.bodysplash.mongolink.domain;

import com.mongodb.DBCursor;

public class CursorParameter {
    public static CursorParameter empty() {
        return new EmptyCursorParamter();
    }

    public static CursorParameter withSkip(int skip) {
        return new CursorParameter().skip(skip);
    }

    public static CursorParameter withLimit(int limit) {
        return new CursorParameter().limit(limit);
    }

    protected CursorParameter() {

    }

    DBCursor apply(DBCursor cursor) {
        cursor = cursor.limit(limit).skip(skip);
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

    private int skip;
    private int limit;
}
