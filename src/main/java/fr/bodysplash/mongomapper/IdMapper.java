package fr.bodysplash.mongomapper;


import java.lang.reflect.Method;

public class IdMapper extends PropertyMapper{
    
    public IdMapper(String name, Method method) {
        super(name, method);
    }

    @Override
    protected String dbFieldName() {
        return "_id";
    }
}
