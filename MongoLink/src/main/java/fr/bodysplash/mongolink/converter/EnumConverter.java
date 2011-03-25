package fr.bodysplash.mongolink.converter;

public class EnumConverter extends Converter {

    private Class<? extends Enum> enumType;

    public EnumConverter(Class<?> enumType) {
        this.enumType = (Class<? extends Enum>) enumType;
    }

    @Override
    public Object toDbValue(Object value) {
        return value.toString();
    }

    @Override
    public Object fromDbValue(Object value) {
        return Enum.valueOf(enumType, value.toString());
    }

}
