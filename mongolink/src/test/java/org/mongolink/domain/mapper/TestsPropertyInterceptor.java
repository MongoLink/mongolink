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
import org.mongolink.test.entity.FakeAggregate;
import org.mongolink.utils.FieldContainer;

import java.lang.reflect.Method;

import static org.mockito.Mockito.*;

public class TestsPropertyInterceptor {

    @Test
    public void setLastMethodToClassMap() throws Throwable {
        AggregateMap aggregateMap = mock(AggregateMap.class);
        PropertyInterceptor interceptor = new PropertyInterceptor(aggregateMap);
        Method method = FakeAggregate.class.getDeclaredMethod("getId");

        interceptor.intercept(new Object(), method, null, null);

        verify(aggregateMap).setLastMethod(new FieldContainer(method));
    }

    @Test
    public void ignoreGroovyMethod() throws Throwable {
        ClassMap classMap = mock(ClassMap.class);
        PropertyInterceptor interceptor = new PropertyInterceptor(classMap);
        Method method = EntityWithGroovyMethod.class.getDeclaredMethod("$getStuff");

        interceptor.intercept(new EntityWithGroovyMethod(), method, null, null);

        verifyZeroInteractions(classMap);
    }

    public static class EntityWithGroovyMethod {

        public void $getStuff() {
            throw new RuntimeException();
        }
    }

}
