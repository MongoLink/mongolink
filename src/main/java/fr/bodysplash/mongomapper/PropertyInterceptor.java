package fr.bodysplash.mongomapper;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;


public class PropertyInterceptor implements MethodInterceptor {
    private Mapping<?> mapping;


    public PropertyInterceptor(Mapping<?> Mapping) {
        mapping = Mapping;
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        if (mapping.isProperty()) {
            mapProperty(method);
        }
        if (mapping.isCollection()) {
            mapCollection(method);
        }
        return null;
    }

    private void mapProperty(Method method) {
        mapping.addProperty(methodName(method), method);
    }

    private void mapCollection(Method method) {
        mapping.addCollection(methodName(method), method);

    }

    private String methodName(Method method) {
        return method.getName().substring(3, method.getName().length()).toLowerCase();
    }

}
