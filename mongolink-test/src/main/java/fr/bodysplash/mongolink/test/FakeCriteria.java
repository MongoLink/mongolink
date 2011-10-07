package fr.bodysplash.mongolink.test;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mongodb.DBObject;
import com.mongodb.FakeDB;
import com.mongodb.FakeDBCollection;
import fr.bodysplash.mongolink.domain.QueryExecutor;
import fr.bodysplash.mongolink.domain.criteria.Criteria;
import fr.bodysplash.mongolink.domain.criteria.Restriction;
import fr.bodysplash.mongolink.test.criteria.FakeRestriction;

import java.util.List;

public class FakeCriteria<T> extends Criteria<T> {

    public FakeCriteria(QueryExecutor executor) {
        super(executor);
    }

    @Override
    public List<T> list() {
        FakeDB db = (FakeDB) getExecutor().getDb();
        final FakeDBCollection collection = (FakeDBCollection) db.getCollection(getExecutor().getEntityMapper().collectionName());
        return Lists.newArrayList(Iterables.transform(filter(collection), new Function<DBObject, T>() {
            @Override
            public T apply(DBObject input) {
                return (T) getExecutor().getEntityMapper().toInstance(input);
            }
        }));
    }



    private Iterable<DBObject> filter(FakeDBCollection collection) {
        return Iterables.filter(collection.getObjects(), new Predicate<DBObject>() {
            @Override
            public boolean apply(DBObject input) {
                boolean result = true;
                for (Restriction restriction : getRestrictions()) {
                    result &= ((FakeRestriction) restriction).isSatisfiedBy(input);
                }
                return result;
            }
        });
    }
}
