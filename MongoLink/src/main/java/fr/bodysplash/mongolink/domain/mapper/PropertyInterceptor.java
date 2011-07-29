package fr.bodysplash.mongolink.domain.mapper;

import fr.bodysplash.mongolink.utils.MethodContainer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;


class PropertyInterceptor implements MethodInterceptor {
    private final AbstractMap<?> classMap;

    public PropertyInterceptor(AbstractMap<?> classMap) {
        this.classMap = classMap;
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        classMap.setLastMethod(new MethodContainer(method));
        return null;
    }

}
