package org.mongolink.domain.criteria;

import com.mongodb.*;

import java.util.*;

public class RestrictionInUUID extends Restriction {

    public RestrictionInUUID(String field, List<UUID> uuids) {
        super(field);
        this.uuids = uuids;
    }

    @Override
    public void apply(final DBObject query) {
        final BasicDBObject object = new BasicDBObject();
        final BasicDBList values = new BasicDBList();
        for (UUID uuid : uuids) {
            values.add(uuid);
        }
        object.put("$in", values);
        query.put(getField(), object);
    }

    private List<UUID> uuids;
}
