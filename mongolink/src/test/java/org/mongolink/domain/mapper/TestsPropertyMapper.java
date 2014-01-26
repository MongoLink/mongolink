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

import com.mongodb.BasicDBObject;
import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.mongolink.test.entity.Comment;
import org.mongolink.test.entity.FakeAggregate;
import org.mongolink.test.simpleMapping.CommentMapping;
import org.mongolink.utils.FieldContainer;
import org.mongolink.utils.Fields;

import java.lang.reflect.*;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class TestsPropertyMapper {

    public enum TestEnum {
        good, bad
    }

    public static class FakeEntity {
        private TestEnum value;
        private int primitive;
        private DateTime creationDate;
        private LocalDate dateOfDay;
        private boolean ok;

        public DateTime getCreationDate() {
            return creationDate;
        }

        public void setCreationDate(final DateTime creationDate) {
            this.creationDate = creationDate;
        }

        public LocalDate getDateOfDay() {
            return dateOfDay;
        }

        public void setDateOfDay(LocalDate dateOfDay) {
            this.dateOfDay = dateOfDay;
        }

        public int getPrimitive() {
            return primitive;
        }

        public TestEnum getValue() {
            return value;
        }

        public boolean isOk() {
            return ok;
        }

    }

    @Test
    public void canSaveAnEnum() throws NoSuchMethodException {
        PropertyMapper mapper = mapperForEnum();
        FakeEntity entity = new FakeEntity();
        entity.value = TestsPropertyMapper.TestEnum.good;
        BasicDBObject object = new BasicDBObject();

        mapper.save(entity, object);

        assertThat(object.get("value"), is((Object) "good"));
    }

    @Test
    public void canPopulateAnEnum() throws NoSuchMethodException {
        BasicDBObject object = new BasicDBObject();
        object.put("value", "bad");
        PropertyMapper mapper = mapperForEnum();
        FakeEntity instance = new FakeEntity();

        mapper.populate(instance, object);

        assertThat(instance.getValue(), is(TestEnum.bad));
    }

    private PropertyMapper mapperForEnum() throws NoSuchMethodException {
        PropertyMapper propertyMapper = new PropertyMapper(new FieldContainer(FakeEntity.class.getDeclaredMethod("getValue")));
        propertyMapper.setMapper(parentMapper());
        return propertyMapper;
    }

    @Test
    public void canSavePrimitiveType() throws NoSuchMethodException {
        PropertyMapper mapper = mapperForProperty();
        FakeEntity entity = new FakeEntity();
        entity.primitive = 10;
        BasicDBObject object = new BasicDBObject();

        mapper.save(entity, object);

        assertThat(object.get("primitive"), is((Object) 10));
    }

    private PropertyMapper mapperForProperty() throws NoSuchMethodException {
        final PropertyMapper propertyMapper = new PropertyMapper(new FieldContainer(primitiveGetter()));
        propertyMapper.setMapper(parentMapper());
        return propertyMapper;
    }

    private Method primitiveGetter() throws NoSuchMethodException {
        return FakeEntity.class.getDeclaredMethod("getPrimitive");
    }

    @Test
    public void canSaveDateTimeType() throws NoSuchMethodException {
        PropertyMapper mapper = propertyMapperForDateTime();
        FakeEntity fakeEntity = new FakeEntity();
        DateTime now = new DateTime();
        fakeEntity.setCreationDate(now);
        BasicDBObject basicDBObject = new BasicDBObject();

        mapper.save(fakeEntity, basicDBObject);

        assertThat(basicDBObject.get("creationDate"), is((Object) now.getMillis()));
    }

    @Test
    public void canPopulateDateTimeType() throws NoSuchMethodException {
        BasicDBObject object = new BasicDBObject();
        DateTime dateTime = new DateTime();
        object.put("creationDate", dateTime.getMillis());
        PropertyMapper propertyMapper = propertyMapperForDateTime();
        FakeEntity instance = new FakeEntity();

        propertyMapper.populate(instance, object);

        assertThat(instance.getCreationDate(), is(dateTime));
    }

    private PropertyMapper propertyMapperForDateTime() throws NoSuchMethodException {
        final PropertyMapper result = new PropertyMapper(new FieldContainer(FakeEntity.class.getDeclaredMethod("getCreationDate")));
        result.setMapper(parentMapper());
        return result;
    }

    @Test
    public void canSaveLocalDateType() throws NoSuchMethodException {
        PropertyMapper mapper = propertyMapperForLocalDate();
        FakeEntity fakeEntity = new FakeEntity();
        LocalDate now = new LocalDate();
        fakeEntity.setDateOfDay(now);
        BasicDBObject basicDBObject = new BasicDBObject();

        mapper.save(fakeEntity, basicDBObject);

        assertThat(basicDBObject.get("dateOfDay"), is((Object) now.toDateTimeAtStartOfDay().getMillis()));
    }

    @Test
    public void canPopulateLocalDateType() throws NoSuchMethodException {
        BasicDBObject object = new BasicDBObject();
        LocalDate localDate = new LocalDate();
        object.put("dateOfDay", localDate.toDateTimeAtStartOfDay().getMillis());
        PropertyMapper propertyMapper = propertyMapperForLocalDate();
        FakeEntity instance = new FakeEntity();

        propertyMapper.populate(instance, object);

        assertThat(instance.getDateOfDay(), is(localDate));
    }

    private PropertyMapper propertyMapperForLocalDate() throws NoSuchMethodException {
        final PropertyMapper result = new PropertyMapper(new FieldContainer(FakeEntity.class.getDeclaredMethod("getDateOfDay")));
        result.setMapper(parentMapper());
        return result;
    }

    private AggregateMapper<FakeEntity> parentMapper() {
        AggregateMapper<FakeEntity> mapper = new AggregateMapper<FakeEntity>(FakeEntity.class);
        final MapperContext context = new MapperContext();
        context.addMapper(mapper);
        return mapper;
    }

    @Test
    public void canSaveBooleanType() throws NoSuchMethodException {
        PropertyMapper mapper = propertyMapperForBoolean();
        FakeEntity fakeEntity = new FakeEntity();
        fakeEntity.ok = true;
        BasicDBObject basicDBObject = new BasicDBObject();

        mapper.save(fakeEntity, basicDBObject);

        assertThat(basicDBObject.get("k"), nullValue());
        assertThat(basicDBObject.get("ok"), is((Object) true));
    }

    private PropertyMapper propertyMapperForBoolean() throws NoSuchMethodException {
        final PropertyMapper propertyMapper = new PropertyMapper(new FieldContainer(primitiveBooleanGetter()));
        propertyMapper.setMapper(parentMapper());
        return propertyMapper;
    }

    private Method primitiveBooleanGetter() throws NoSuchMethodException {
        return FakeEntity.class.getDeclaredMethod("isOk");
    }

    @Test
    public void canSerializeToDBOject() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        final BasicDBObject into = new BasicDBObject();
        FakeAggregate entity = new FakeAggregate("te");
        final Comment comment = new Comment("tes");
        entity.setComment(comment);

        propertyMapperForComponent().save(entity, into);

        final BasicDBObject commentDB = (BasicDBObject) into.get("comment");
        assertThat(commentDB, notNullValue());
        assertThat(commentDB.get("value").toString(), Matchers.is("tes"));
    }

    @Test
    public void canPopulate() throws NoSuchMethodException {
        final BasicDBObject from = new BasicDBObject();
        final BasicDBObject val = new BasicDBObject();
        val.put("value", "valeur");
        from.put("comment", val);
        FakeAggregate instance = new FakeAggregate("kjklj");

        propertyMapperForComponent().populate(instance, from);

        assertThat(instance.getComment().getValue(), Matchers.is("valeur"));
    }


    @Test
    public void canCreateFromField() throws NoSuchFieldException {
        FakeAggregate entity = new FakeAggregate("value");
        BasicDBObject dbObject = new BasicDBObject();

        propertyMapperFromField().save(entity, dbObject);

        assertThat(dbObject.getString("value"), Matchers.is("value"));
    }

    private PropertyMapper propertyMapperFromField() throws  NoSuchFieldException {
        Field field = Fields.find(FakeAggregate.class, "value");
        final PropertyMapper result = new PropertyMapper(new FieldContainer(field));
        result.setMapper(parentMapper());
        return result;
    }

    public PropertyMapper propertyMapperForComponent() throws NoSuchMethodException {
        MapperContext context = new MapperContext();
        CommentMapping mapping = new CommentMapping();
        mapping.buildMapper(context);
        final ClassMapper classMapper = mock(ClassMapper.class);
        when(classMapper.getContext()).thenReturn(context);
        when(classMapper.getPersistentType()).thenReturn(FakeAggregate.class);
        final Method method = FakeAggregate.class.getMethod("getComment", null);
        FieldContainer fieldContainer = new FieldContainer(method);
        PropertyMapper propertyComponentMapper = new PropertyMapper(fieldContainer);
        propertyComponentMapper.setMapper(classMapper);
        return propertyComponentMapper;
    }

}
