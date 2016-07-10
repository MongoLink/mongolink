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
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.*;

public class DiffStrategy extends UpdateStrategy {

    @Override
    public void update(Document initialValue, Document updatedValue, MongoCollection<Document> collection) {
        final Document diff = new DbObjectDiff(initialValue).compareWith(updatedValue);
        final Bson q = updateQuery(initialValue);
        executePushAndPull(collection, diff, q);
        if (!diff.keySet().isEmpty()) {
            LOGGER.debug("Updating : collection {} : query {} : modifiers : {}", collection.getNamespace().getCollectionName(), q, diff);
            collection.updateOne(q, diff);
        }
    }

    private void executePushAndPull(MongoCollection<Document> collection, Document diff, Bson q) {
        // concurrent modififications on the same array is quite tricky
        // we can't $set, $push or $pull at the same time, mainly because $set uses index to do the job
        // so we have to split operations in three distinct update operations
        // see https://jira.mongodb.org/browse/SERVER-1014
        execute(DbObjectDiff.Modifier.PULL, collection, diff, q);
        execute(DbObjectDiff.Modifier.PUSH, collection, diff, q);
        execute(DbObjectDiff.Modifier.PUSHALL, collection, diff, q);
    }

    private void execute(DbObjectDiff.Modifier modifier, MongoCollection<Document> collection, Document diff, Bson q) {
        final Document modifications = (Document) diff.get(modifier.toString());
        diff.remove(modifier.toString());
        if (modifications != null) {
            LOGGER.debug("Updating array : {} modifier: {}  values: {}", q, modifier, modifications);
            collection.updateOne(q, new BasicDBObject(modifier.toString(), modifications));
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DiffStrategy.class);
}
