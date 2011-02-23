package fr.bodysplash.mongomapper.mapper;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Method;


class PropertyInterceptor implements MethodInterceptor {
    private final ClassMap<?> classMap;

    public PropertyInterceptor(ClassMap<?> classMap) {
        this.classMap = classMap;
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        if (classMap.isId()) {
            mapId(method);
        }
        if (classMap.isProperty()) {
            mapProperty(method);
        }
        if (classMap.isCollection()) {
            mapCollection(method);
        }
        return null;
    }

    private void mapId(Method method) {
        classMap.setId(methodName(method), method);
    }

    private void mapProperty(Method method) {
        classMap.addProperty(methodName(method), method);
    }

    private void mapCollection(Method method) {
        classMap.addCollection(methodName(method), method);

    }

    private String methodName(Method method) {
        return StringUtils.uncapitalize(method.getName().substring(3, method.getName().length()));
    }

}
