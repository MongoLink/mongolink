package fr.bodysplash.mongolink.domain;

import com.mongodb.DBCursor;

public class EmptyCursorParameter extends CursorParameter {

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

    @Override
    public CursorParameter sort(final String sortField, final int sortOrder) {
        return new CursorParameter().sort(sortField, sortOrder);
    }
}
