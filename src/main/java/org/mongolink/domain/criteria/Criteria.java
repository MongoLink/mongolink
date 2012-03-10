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

package org.mongolink.domain.criteria;

import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.mongolink.domain.CursorParameter;
import org.mongolink.domain.QueryExecutor;

import java.util.Collections;
import java.util.List;

@SuppressWarnings("unchecked")
public class Criteria<T> {

    public Criteria(QueryExecutor executor) {
        this.executor = executor;
    }

    public List<T> list() {
        return executor.execute(createQuery(), parameter);
    }

    public void add(Restriction restriction) {
        restrictions.add(restriction);
    }

    DBObject createQuery() {
        final BasicDBObject result = new BasicDBObject();
        for (Restriction restriction : restrictions) {
            restriction.apply(result);
        }
        return result;
    }

    protected List<Restriction> getRestrictions() {
        return Collections.unmodifiableList(restrictions);
    }

    protected QueryExecutor getExecutor() {
        return executor;
    }

    public void limit(int limit) {
        parameter = parameter.limit(limit);
    }

    public void skip(int skip) {
        parameter = parameter.skip(skip);
    }

    public void sort(final String sortField, final int sortOrder) {
        parameter = parameter.sort(sortField, sortOrder);
    }

    protected CursorParameter getCursorParameter() {
        return parameter;
    }

    private List<Restriction> restrictions = Lists.newArrayList();
    private QueryExecutor executor;
    private CursorParameter parameter = CursorParameter.empty();
}
