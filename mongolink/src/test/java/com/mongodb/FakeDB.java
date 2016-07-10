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

package com.mongodb;

import com.mongodb.client.MongoCollection;
import com.mongodb.operation.OperationExecutor;
import org.bson.codecs.configuration.*;

import static org.mockito.Mockito.*;


public class FakeDB extends MongoDatabaseImpl {

    public FakeDB() {
        super("test", mock(CodecRegistry.class), ReadPreference.nearest(), WriteConcern.ACKNOWLEDGED, ReadConcern.DEFAULT, mock(OperationExecutor.class));
    }

    @Override
    public <TDocument> MongoCollection<TDocument> getCollection(String collectionName, Class<TDocument> tDocumentClass) {
        return mock(MongoCollection.class);
    }


}
