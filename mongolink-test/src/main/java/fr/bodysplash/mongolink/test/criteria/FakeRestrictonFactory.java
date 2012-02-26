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

package fr.bodysplash.mongolink.test.criteria;

import fr.bodysplash.mongolink.domain.criteria.Restriction;
import fr.bodysplash.mongolink.domain.criteria.RestrictionBetween;
import fr.bodysplash.mongolink.domain.criteria.RestrictionFactory;

public class FakeRestrictonFactory extends RestrictionFactory {

    @Override
    public Restriction getEquals(String field, Object value) {
        return new FakeRestrictionEquals(field, value);
    }

    @Override
    public RestrictionBetween getBetween(String field, Object start, Object end) {
        return new FakeRestrictionBetween(field, start, end);
    }

    @Override
    public Restriction getNotEquals(final String field, final Object value) {
        return new FakeRestrictionNotEquals(field, value);
    }
}
