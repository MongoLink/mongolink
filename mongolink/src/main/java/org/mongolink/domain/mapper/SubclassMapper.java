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


import com.mongodb.DBObject;
import org.mongolink.domain.criteria.CompositeRestriction;
import org.mongolink.domain.criteria.Restrictions;

public class SubclassMapper<T> extends ClassMapper<T> {

    public static String discriminatorValue(DBObject from) {
        Object discriminator = from.get(DISCRIMINATOR);
        return discriminator == null ? "" : discriminator.toString();
    }

    public SubclassMapper(Class<T> type) {
        super(type);
    }

    @Override
    public void save(Object instance, DBObject into) {
        super.save(instance, into);
        into.put(DISCRIMINATOR, discriminator());
    }

    String discriminator() {
        return getPersistentType().getSimpleName();
    }

    public void applyRestrictions(Class<?> target, CompositeRestriction restriction) {
        if(target == getPersistentType() || target.isAssignableFrom(getPersistentType())) {
            restriction.with(Restrictions.equals(DISCRIMINATOR, discriminator()));
        }
        for (SubclassMapper<?> subclassMapper : getSubclasses()) {
            subclassMapper.applyRestrictions(target, restriction);
        }
    }

    public static final String DISCRIMINATOR = "__discriminator";
}
