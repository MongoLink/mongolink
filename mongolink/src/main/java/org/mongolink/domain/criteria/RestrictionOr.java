/*
 * MongoLink, Object Document Mapper for Java and MongoDB
 *
 * Copyright (c) 2012, Arpinum or third-party contributors as
 * indicated by the contributors.txt file
 *
 * MongoLink is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * MongoLink is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the Lesser GNU General Public License
 * along with MongoLink.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.mongolink.domain.criteria;

import com.google.common.collect.Lists;
import org.bson.Document;

import java.util.List;

public class RestrictionOr extends CompositeRestriction {
    public RestrictionOr() {
        super("");
    }

    @Override
    public void apply(Document query) {
        List<Object> list = Lists.newArrayList();
        for (Restriction restriction : getRestrictions()) {
            Document subquery = new Document();
            restriction.apply(subquery);
            list.add(subquery);
        }

        query.put("$or", list);
    }

}
