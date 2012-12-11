package org.mongolink.domain.mapper;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class Capped {

    public boolean isCapped() {
        return true;
    }

    public Capped withSize(final int cappedSize) {
        this.size = cappedSize;
        return this;
    }

    public Capped withMax(final int max) {
        this.max = max;
        return this;
    }

    public DBObject getDbValue() {
        final BasicDBObject result = new BasicDBObject();
        result.put("capped", "true");
        result.put("size", size);
        result.put("max", max);
        return result;
    }

    public int getMax() {
        return max;
    }

    public int getSize() {
        return size;
    }

    private int size;
    private int max;

}