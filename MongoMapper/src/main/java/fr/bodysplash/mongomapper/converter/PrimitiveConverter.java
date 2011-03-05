package fr.bodysplash.mongomapper.converter;

public class PrimitiveConverter extends Converter {
    @Override
    public Object toDbValue(Object value) {
        return value;
    }

    @Override
    public Object fromDbValue(Object value) {
        return value;
    }
}
