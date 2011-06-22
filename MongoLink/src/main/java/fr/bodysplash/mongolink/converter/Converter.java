package fr.bodysplash.mongolink.converter;

import org.joda.time.DateTime;

import java.lang.reflect.Method;

public abstract class Converter {

    private static final PrimitiveConverter PRIMITIVE_CONVERTER = new PrimitiveConverter();

    public static Converter forMethod(Method method) {
        return forType(method.getReturnType());
    }

    public static Converter forType(Class<?> type) {
        if (isEnum(type)) {
            return new EnumConverter(type);
        }
        if (isDateTime(type)) {
            return new DateTimeConverter();
        }
        return PRIMITIVE_CONVERTER;
    }

    private static boolean isDateTime(Class<?> type) {
        return DateTime.class.isAssignableFrom(type);
    }

    private static boolean isEnum(Class<?> type) {
        return type.isEnum() || (type.getSuperclass() != null && type.getSuperclass().isEnum());
    }

    public abstract Object toDbValue(Object value);

    public abstract Object fromDbValue(Object value);
}
