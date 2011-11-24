package fr.bodysplash.mongolink.test;

import com.google.common.base.*;
import com.google.common.collect.*;
import com.mongodb.*;
import fr.bodysplash.mongolink.domain.*;
import fr.bodysplash.mongolink.domain.criteria.*;
import fr.bodysplash.mongolink.test.criteria.FakeRestriction;

import java.util.List;

public class FakeCriteria<T> extends Criteria<T> {

    public FakeCriteria(QueryExecutor executor) {
        super(executor);
    }

    @Override
    public List<T> list() {
        FakeDB db = (FakeDB) getExecutor().getDb();
        final FakeDBCollection collection = (FakeDBCollection) db.getCollection(collectionName());
        return Lists.newArrayList(applyParameters(Iterables.transform(filter(collection), toInstance())));
    }

    private Iterable<T> applyParameters(Iterable<T> list) {
        return applyLimit(applySkip(list));
    }

    private Iterable<T> applySkip(Iterable<T> list) {
        final CursorParameter parameter = getCursorParameter();
        if (parameter.getSkip() == 0) {
            return list;
        }
        return Iterables.skip(list, parameter.getSkip());
    }

    private Iterable<T> applyLimit(Iterable<T> list) {
        final CursorParameter parameter = getCursorParameter();
        if (parameter.getLimit() == 0) {
            return list;
        }
        return Iterables.limit(list, parameter.getLimit());
    }

    private String collectionName() {
        return getExecutor().getEntityMapper().collectionName();
    }

    private Function<DBObject, T> toInstance() {
        return new Function<DBObject, T>() {
            @Override
            public T apply(DBObject input) {
                return (T) getExecutor().getEntityMapper().toInstance(input);
            }
        };
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
