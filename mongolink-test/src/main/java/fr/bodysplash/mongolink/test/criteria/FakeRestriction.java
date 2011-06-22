package fr.bodysplash.mongolink.test.criteria;

import com.mongodb.DBObject;

public interface FakeRestriction {

    boolean isSatisfiedBy(DBObject entity);
}
