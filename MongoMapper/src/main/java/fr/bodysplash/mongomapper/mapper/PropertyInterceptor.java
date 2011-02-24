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
        classMap.setLastMethod(method);
        return null;
    }

    private String methodName(Method method) {
        return StringUtils.uncapitalize(method.getName().substring(3, method.getName().length()));
    }

}
