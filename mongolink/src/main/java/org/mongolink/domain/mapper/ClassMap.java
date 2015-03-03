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

import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import net.sf.cglib.core.DefaultGeneratorStrategy;
import net.sf.cglib.proxy.Enhancer;
import org.mongolink.utils.FieldContainer;
import org.mongolink.utils.Fields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.*;

@SuppressWarnings("unchecked")
public abstract class ClassMap<T> {

    public ClassMap() {
        interceptor = createInterceptor(persistentType());
    }

    protected T createInterceptor(Class<T> type) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(type);
        enhancer.setStrategy(new DefaultGeneratorStrategy());
        enhancer.setCallback(new PropertyInterceptor(this));
        return (T) enhancer.create();
    }

    void setLastMethod(FieldContainer lastMethod) {
        this.lastMethod = lastMethod;
    }

    protected T element() {
        return interceptor;
    }

    public final Class<T> persistentType() {
        return (Class<T>) typeToken.getRawType();
    }

    @Deprecated
    protected void property(Object value) {
        property().onProperty(value);
    }

    protected PropertyMap property() {
        return new PropertyMap();
    }

    protected CollectionMap collection() {
        return new CollectionMap();
    }

    @Deprecated
    protected void collection(Object value) {
        collection().onProperty(value);
    }

    @SuppressWarnings("UnusedParameters")
    protected void map(Object value) {
        LOGGER.debug("Mapping map : {}", lastMethod);
        getMapper().addMap(new MapMapper(lastMethod));
    }

    protected <U extends T> void subclass(SubclassMap<U> subclassMap) {
        LOGGER.debug("Mapping subclass : {}", subclassMap.persistentType().getSimpleName());
        subclasses.add(subclassMap);
    }

    public void buildMapper(MapperContext context) {
        map();
        for (SubclassMap<?> subclass : subclasses) {
            subclass.buildMapper(context);
            getMapper().addSubclass(subclass.getMapper());
        }
        addMapperToContext(context);
    }

    protected void addMapperToContext(MapperContext context) {
        context.addMapper(getMapper());
    }

    public abstract void map();

    FieldContainer getLastMethod() {
        return lastMethod;
    }

    protected abstract ClassMapper<T> getMapper();

    protected final FieldContainer fieldContainer(String fieldName) {
        return new FieldContainer(Fields.find(persistentType(), fieldName));
    }

    private final TypeToken<T> typeToken = new TypeToken<T>(getClass()) {
    };

    public class PropertyMap {

        public void onField(String fieldName) {
            addMapper(fieldContainer(fieldName));
        }

        @SuppressWarnings("UnusedParameters")
        @Deprecated
        public void onProperty(Object value) {
            addMapper(lastMethod);
        }

        public void onProperty(Consumer<T> consumer) {
            consumer.accept(element());
            addMapper(lastMethod);
        }

        private void addMapper(FieldContainer field) {
            LOGGER.debug("Mapping property : {}", field);
            getMapper().addProperty(new PropertyMapper(field));
        }

    }

    public class CollectionMap {

        @SuppressWarnings("UnusedDeclaration")
        public void onField(String fieldName) {
            addMapper(fieldContainer(fieldName));
        }

        @SuppressWarnings("UnusedParameters")
        @Deprecated
        public void onProperty(Object value) {
            addMapper(lastMethod);
        }

        public void onProperty(Consumer<T> consumer) {
            consumer.accept(element());
            addMapper(lastMethod);
        }

        private void addMapper(FieldContainer field) {
            LOGGER.debug("Mapping collection : {}", field);
            getMapper().addCollection(new CollectionMapper(field));
        }

    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassMap.class);
    private FieldContainer lastMethod;
    private final T interceptor;
    private final List<SubclassMap<? extends T>> subclasses = Lists.newArrayList();
}
