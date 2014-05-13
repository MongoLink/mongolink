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
 */

package org.mongolink.domain.session;


import com.mongodb.BasicDBObject;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class TestsUnitOfWork {

    @Test
    public void canRetrieveSubtype() {
        UnitOfWork unitOfWork = new UnitOfWork(mock(MongoSessionImpl.class));
        Child entity = new Child();
        unitOfWork.registerDirty(1, entity, new BasicDBObject());

        Object entityFound = unitOfWork.getEntity(Parent.class, 1);

        assertThat(entityFound).isNotNull();
    }

    @Test
    public void canCheckExistenceWithSubType() {
        final UnitOfWork unitOfWork = new UnitOfWork(mock(MongoSessionImpl.class));
        final Child child = new Child();
        unitOfWork.registerDirty(1, child, new BasicDBObject());

        assertTrue(unitOfWork.contains(Parent.class, 1));
    }

    private class Parent {

    }

    private class Child extends Parent {

    }
}
