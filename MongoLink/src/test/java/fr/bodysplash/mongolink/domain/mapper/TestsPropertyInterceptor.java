package fr.bodysplash.mongolink.domain.mapper;

import fr.bodysplash.mongolink.test.entity.FakeEntity;
import fr.bodysplash.mongolink.utils.MethodContainer;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.mockito.Mockito.*;

public class TestsPropertyInterceptor {

    @Test
    public void setLastMethodToClassMap() throws Throwable {
        ClassMap classMap = mock(ClassMap.class);
        PropertyInterceptor interceptor = new PropertyInterceptor(classMap);
        Method method = FakeEntity.class.getDeclaredMethod("getId");

        interceptor.intercept(null, method, null, null);

        verify(classMap).setLastMethod(new MethodContainer(method));
    }
}
