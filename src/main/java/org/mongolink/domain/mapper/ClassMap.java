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

import net.sf.cglib.core.DefaultGeneratorStrategy;
import net.sf.cglib.proxy.Enhancer;
import org.apache.log4j.Logger;
import org.mongolink.utils.MethodContainer;

@SuppressWarnings("unchecked")
public abstract class ClassMap<T> {

    public ClassMap(Class<T> type) {
        this.type = type;
        LOGGER.debug("Mapping " + getType());
        interceptor = createInterceptor(type);
    }

    protected T createInterceptor(Class<T> type) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(type);
        enhancer.setStrategy(new DefaultGeneratorStrategy());
        enhancer.setCallback(new PropertyInterceptor(this));
        return (T) enhancer.create();
    }

    void setLastMethod(MethodContainer lastMethod) {
        this.lastMethod = lastMethod;
    }

    protected T element() {
        return interceptor;
    }

    public Class<T> getType() {
        return type;
    }

    protected void property(Object value) {
        String name = lastMethod.shortName();
        LOGGER.debug("Mapping property " + name);
        getMapper().addProperty(new PropertyMapper(lastMethod));
    }

    protected void collection(Object value) {
        LOGGER.debug("Mapping collection:" + lastMethod.shortName());
        getMapper().addCollection(new CollectionMapper(lastMethod));
    }

    protected void hashmap(Object value) {
        LOGGER.debug("Mapping hashmap:" + lastMethod.shortName());
        getMapper().addHashmap(new HashmapMapper(lastMethod));
    }

    public void buildMapper(MapperContext context) {
        map();
        context.addMapper(getMapper());
    }

    protected abstract void map();

    MethodContainer getLastMethod() {
        return lastMethod;
    }

    protected void setCapped(boolean value, int cappedSize, int cappedMax) {
        getMapper().setCapped(value, cappedSize, cappedMax);
    }

    protected abstract ClassMapper<T> getMapper();

    private static final Logger LOGGER = Logger.getLogger(EntityMap.class);
    private MethodContainer lastMethod;
    private final Class<T> type;
    private final T interceptor;
}
