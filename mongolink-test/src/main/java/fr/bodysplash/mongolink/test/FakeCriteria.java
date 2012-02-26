/*
 * MongoLink, Object Document Mapper for Java and MongoDB
 *
 * Copyright (c) 2012, Arpinum or third-party contributors as
 * indicated by the @author tags
 *
 * MongoLink is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MongoLink is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the Lesser GNU General Public License
 * along with MongoLink.  If not, see <http://www.gnu.org/licenses/>. 
 *
 */

package fr.bodysplash.mongolink.test;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mongodb.DBObject;
import com.mongodb.FakeDB;
import com.mongodb.FakeDBCollection;
import fr.bodysplash.mongolink.domain.CursorParameter;
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
