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

package org.mongolink.domain.updateStrategy;

import com.mongodb.*;
import org.apache.log4j.Logger;

public class DiffStrategy extends UpdateStrategy {

    @Override
    public void update(DBObject initialValue, DBObject update, DBCollection collection) {
        final DBObject diff = new DbObjectDiff(initialValue).compareWith(update);
        if (!diff.keySet().isEmpty()) {
            final DBObject q = updateQuery(initialValue);
            final DBObject pull = (DBObject) diff.get(DbObjectDiff.Modifier.PULL.toString());
            diff.removeField(DbObjectDiff.Modifier.PULL.toString());
            LOGGER.debug("Updating query:" + q + " values: " + diff);
            collection.update(q, diff);
            // ugly hack to support removing element by index
            // see https://jira.mongodb.org/browse/SERVER-1014
            if (pull != null) {
                LOGGER.debug("Cleaning collection:" + q + " values: " + pull);
                collection.update(q, new BasicDBObject(DbObjectDiff.Modifier.PULL.toString(), pull));
            }
        }
    }

    private static final Logger LOGGER = Logger.getLogger(DiffStrategy.class);
}
