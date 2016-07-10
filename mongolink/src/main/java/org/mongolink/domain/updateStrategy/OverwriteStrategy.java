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

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.slf4j.*;

public class OverwriteStrategy extends UpdateStrategy {

    @Override
    public void update(Document initialValue, Document updatedValue, MongoCollection<Document> collection) {
        LOGGER.debug("Updating : collection {} : element {}", collection.getNamespace().getCollectionName(), updatedValue);
        collection.replaceOne(updateQuery(initialValue), updatedValue);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(OverwriteStrategy.class);
}
