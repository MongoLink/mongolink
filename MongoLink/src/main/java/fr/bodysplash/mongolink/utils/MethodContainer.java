package fr.bodysplash.mongolink.utils;

import com.google.common.base.Objects;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Method;

public class MethodContainer {

    public MethodContainer(Method method) {
        this.method = method;
    }

    public Method getMethod() {
        return method;
    }

    public String shortName() {
        return StringUtils.uncapitalize(method.getName().substring(3, method.getName().length()));
    }

    @Override
    public boolean equals(Object o) {
        if(o == null) {
            return false;
        }
        if(!(o instanceof MethodContainer)) {
            return false;
        }
        MethodContainer other = (MethodContainer) o;
        return Objects.equal(method, other.method);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(method);
    }

    private Method method;
}
