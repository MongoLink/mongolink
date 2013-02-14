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

package org.mongolink.domain.mapper;

import org.junit.Test;
import org.mongolink.test.entity.*;
import org.mongolink.test.inheritanceMapping.*;
import org.mongolink.test.simpleMapping.*;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class TestsAggregateMap {

    @Test
    public void canBuildMapper() {
        FakeAggregateMapping mapping = new FakeAggregateMapping();
        MapperContext context = new MapperContext();

        mapping.buildMapper(context);

        assertThat(context.mapperFor(FakeAggregate.class), notNullValue());
        ClassMapper<FakeAggregate> mapper = context.mapperFor(FakeAggregate.class);

    }

    @Test
    public void providesInterceptor() {
        FakeAggregateMapping mapping = new FakeAggregateMapping();

        assertThat(mapping.element(), notNullValue());
    }

    @Test
    public void subclassIsAddedToTheContext() {
        FakeAggregateWithSubclassMapping mapping = new FakeAggregateWithSubclassMapping();
        MapperContext context = new MapperContext();

        mapping.buildMapper(context);

        assertThat(context.mapperFor(FakeChildAggregate.class), notNullValue());
    }

    @Test
    public void canHaveSeveralSubclasses() {
        FakeAggregateWithTwoSubclassMapping mapping = new FakeAggregateWithTwoSubclassMapping();
        MapperContext context = new MapperContext();

        mapping.buildMapper(context);

        assertThat(context.mapperFor(FakeChildAggregate.class), notNullValue());
        assertThat(context.mapperFor(OtherFakeChildAggregate.class), notNullValue());
    }

    @Test
    public void canSetCap() {
        FakeAggregateMappingWithCap mapping = new FakeAggregateMappingWithCap();
        MapperContext context = new MapperContext();

        mapping.buildMapper(context);

        assertThat(context.mapperFor(FakeEntityWithCap.class).isCapped(), is(true));
    }

    @Test
    public void canSetCapSize() {
        FakeAggregateMappingWithCap mapping = new FakeAggregateMappingWithCap();
        MapperContext context = new MapperContext();

        mapping.buildMapper(context);

        AggregateMapper<?> aggregateMapper = (AggregateMapper<?>) context.mapperFor(FakeEntityWithCap.class);
        assertThat(aggregateMapper.getCapped().getSize(), is(1048076));
    }

    @Test
    public void canSetCapMax() {
        FakeAggregateMappingWithCap mapping = new FakeAggregateMappingWithCap();
        MapperContext context = new MapperContext();

        mapping.buildMapper(context);

        AggregateMapper<?> aggregateMapper = (AggregateMapper<?>) context.mapperFor(FakeEntityWithCap.class);
        assertThat(aggregateMapper.getCapped().getMax(), is(50));
    }


}
