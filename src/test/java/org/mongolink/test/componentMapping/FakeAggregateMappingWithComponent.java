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

package org.mongolink.test.componentMapping;

import org.mongolink.domain.mapper.*;
import org.mongolink.test.entity.FakeEntity;


public class FakeAggregateMappingWithComponent extends AggregateMap<FakeEntity> {

    public FakeAggregateMappingWithComponent(AggregateMapper<FakeEntity> mockMapper) {
        super(FakeEntity.class);
        innerMap = mockMapper;
    }

    @Override
    protected void map() {
        property(element().getComment());
    }

    @Override
    protected AggregateMapper<FakeEntity> getMapper() {
        return innerMap;
    }

    private final AggregateMapper<FakeEntity> innerMap;
}
