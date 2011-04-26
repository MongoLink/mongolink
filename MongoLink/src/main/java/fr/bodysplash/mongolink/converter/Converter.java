package fr.bodysplash.mongolink.converter;

import org.joda.time.DateTime;

import java.lang.reflect.Method;

public abstract class Converter {

    private static final PrimitiveConverter PRIMITIVE_CONVERTER = new PrimitiveConverter();

    public static Converter forMethod(Method method) {
        if (method.getReturnType().isEnum()) {
            return new EnumConverter(method.getReturnType());
        }
        if (method.getReturnType().isInstance(new DateTime())) {
            return new DateTimeConverter();
        }
        return PRIMITIVE_CONVERTER;
    }

    public abstract Object toDbValue(Object value);

    public abstract Object fromDbValue(Object value);
}
