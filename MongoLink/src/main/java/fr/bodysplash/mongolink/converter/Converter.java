package fr.bodysplash.mongolink.converter;

import org.joda.time.DateTime;

import java.lang.reflect.Method;

public abstract class Converter {

    private static final PrimitiveConverter PRIMITIVE_CONVERTER = new PrimitiveConverter();

    public static Converter forMethod(Method method) {
        return forType(method.getReturnType());
    }

    public static Converter forType(Class<?> type) {
        if (type.isEnum()) {
            return new EnumConverter(type);
        }
        if (DateTime.class.isAssignableFrom(type)) {
            return new DateTimeConverter();
        }
        return PRIMITIVE_CONVERTER;
    }

    public abstract Object toDbValue(Object value);

    public abstract Object fromDbValue(Object value);
}
