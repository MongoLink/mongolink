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

package org.mongolink.test.factory;

import com.google.common.collect.Maps;
import com.mongodb.FakeDB;
import org.mongolink.DbFactory;

import java.util.Map;

public class FakeDbFactory extends DbFactory {

    @Override
    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public FakeDB get(String dbName) {
        if (!dbs.containsKey(dbName)) {
            dbs.put(dbName, new FakeDB());
        }
        return dbs.get(dbName);
    }

    private final Map<String, FakeDB> dbs = Maps.newHashMap();
    public String host;
    public int port;
}
