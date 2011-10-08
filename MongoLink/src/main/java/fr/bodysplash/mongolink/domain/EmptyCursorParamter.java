package fr.bodysplash.mongolink.domain;

import com.mongodb.DBCursor;

public class EmptyCursorParamter extends CursorParameter {
    @Override
    DBCursor apply(DBCursor cursor) {
        return cursor;
    }

    @Override
    public CursorParameter limit(int limit) {
        return new CursorParameter().limit(limit);
    }

    @Override
    public CursorParameter skip(int skip) {
        return new CursorParameter().skip(skip);
    }
}
