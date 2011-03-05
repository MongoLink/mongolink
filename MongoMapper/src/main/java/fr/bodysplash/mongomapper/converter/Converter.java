package fr.bodysplash.mongomapper.converter;

import java.lang.reflect.Method;

public abstract class Converter {

    private static final PrimitiveConverter PRIMITIVE_CONVERTER = new PrimitiveConverter();

    public static Converter forMethod(Method method) {
        if (method.getReturnType().isEnum()) {
            return new EnumConverter(method.getReturnType());
        }
        return PRIMITIVE_CONVERTER;
    }

    public abstract Object toDbValue(Object value);

    public abstract Object fromDbValue(Object value);
}
