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

package fr.bodysplash.mongolink.domain.mapper;

import fr.bodysplash.mongolink.test.componentMapping.FakeEntityMappingWithComponent;
import fr.bodysplash.mongolink.test.entity.FakeChildEntity;
import fr.bodysplash.mongolink.test.entity.FakeEntity;
import fr.bodysplash.mongolink.test.entity.FakeEntityWithCap;
import fr.bodysplash.mongolink.test.entity.OtherFakeChildEntity;
import fr.bodysplash.mongolink.test.inheritanceMapping.FakeEntityWithSubclassMapping;
import fr.bodysplash.mongolink.test.inheritanceMapping.FakeEntityWithTwoSubclassMapping;
import fr.bodysplash.mongolink.test.referenceMapping.FakeEntityMappingWithReference;
import fr.bodysplash.mongolink.test.simpleMapping.FakeEntityMapping;
import fr.bodysplash.mongolink.test.simpleMapping.FakeEntityMappingWithCap;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class TestsEntityMap {

    @Test
    public void canBuildMapper() {
        FakeEntityMapping mapping = new FakeEntityMapping();
        MapperContext context = new MapperContext();

        mapping.buildMapper(context);

        assertThat(context.mapperFor(FakeEntity.class), notNullValue());
        ClassMapper<FakeEntity> mapper = context.mapperFor(FakeEntity.class);

    }

    @Test
    public void providesInterceptor() {
        FakeEntityMapping mapping = new FakeEntityMapping();

        assertThat(mapping.element(), notNullValue());
    }

    @Test
    public void subclassIsAddedToTheContext() {
        FakeEntityWithSubclassMapping mapping = new FakeEntityWithSubclassMapping();
        MapperContext context = new MapperContext();

        mapping.buildMapper(context);

        assertThat(context.mapperFor(FakeChildEntity.class), notNullValue());
    }

    @Test
    public void canHaveSeveralSubclasses() {
        FakeEntityWithTwoSubclassMapping mapping = new FakeEntityWithTwoSubclassMapping();
        MapperContext context = new MapperContext();

        mapping.buildMapper(context);

        assertThat(context.mapperFor(FakeChildEntity.class), notNullValue());
        assertThat(context.mapperFor(OtherFakeChildEntity.class), notNullValue());
    }

    @Test
    public void canSetCap() {
        FakeEntityMappingWithCap mapping = new FakeEntityMappingWithCap();
        MapperContext context = new MapperContext();

        mapping.buildMapper(context);

        assertThat(context.mapperFor(FakeEntityWithCap.class).isCapped(), is(true));
    }

    @Test
    public void canSetCapSize() {
        FakeEntityMappingWithCap mapping = new FakeEntityMappingWithCap();
        MapperContext context = new MapperContext();

        mapping.buildMapper(context);

        EntityMapper<?> entityMapper = (EntityMapper<?>) context.mapperFor(FakeEntityWithCap.class);
        assertThat(entityMapper.getCappedSize(), is(1048076));
    }

    @Test
    public void canSetCapMax() {
        FakeEntityMappingWithCap mapping = new FakeEntityMappingWithCap();
        MapperContext context = new MapperContext();

        mapping.buildMapper(context);

        EntityMapper<?> entityMapper = (EntityMapper<?>) context.mapperFor(FakeEntityWithCap.class);
        assertThat(entityMapper.getCappedMax(), is(50));
    }

    @Test
    public void canDeclareReference() {
        final EntityMapper<FakeEntity> mockMapper = mock(EntityMapper.class);
        final FakeEntityMappingWithReference map = new FakeEntityMappingWithReference(mockMapper);

        map.buildMapper(new MapperContext());

        ArgumentCaptor<ReferenceMapper> captor = ArgumentCaptor.forClass(ReferenceMapper.class);
        verify(mockMapper).addReference(captor.capture());
        final ReferenceMapper mapper = captor.getValue();
        assertThat(mapper, notNullValue());
    }

}
