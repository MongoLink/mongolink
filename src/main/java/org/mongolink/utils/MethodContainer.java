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

package org.mongolink.utils;

import com.google.common.base.Objects;
import org.apache.commons.lang.StringUtils;
import org.mongolink.MongoLinkError;

import java.lang.reflect.Method;

public class MethodContainer {

    public MethodContainer(Method method) {
        this.method = method;
    }

    public Method getMethod() {
        return method;
    }

    public String shortName() {
        return StringUtils.uncapitalize(method.getName().substring(prefixLength(), method.getName().length()));
    }

    private int prefixLength() {
        if (method.getName().startsWith("is")) {
            return 2;
        }
        return 3;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof MethodContainer)) {
            return false;
        }
        MethodContainer other = (MethodContainer) o;
        return Objects.equal(method, other.method);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(method);
    }

    public Object invoke(final Object instance) {
        try {
            return method.invoke(instance);
        } catch (Exception e) {
            throw new MongoLinkError("Invocation exception : " + shortName() + " " + instance.getClass(), e);
        }
    }

    private Method method;
}
