package fr.bodysplash.mongolink.mapper;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;


class PropertyInterceptor implements MethodInterceptor {
    private final ClassMap<?> classMap;

    public PropertyInterceptor(ClassMap<?> classMap) {
        this.classMap = classMap;
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        classMap.setLastMethod(method);
        return null;
    }

}