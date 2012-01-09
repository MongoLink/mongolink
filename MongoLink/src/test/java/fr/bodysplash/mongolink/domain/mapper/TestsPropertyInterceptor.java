package fr.bodysplash.mongolink.domain.mapper;

import fr.bodysplash.mongolink.test.entity.FakeEntity;
import fr.bodysplash.mongolink.utils.MethodContainer;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class TestsPropertyInterceptor {

    @Test
    public void setLastMethodToClassMap() throws Throwable {
        EntityMap entityMap = mock(EntityMap.class);
        PropertyInterceptor interceptor = new PropertyInterceptor(entityMap);
        Method method = FakeEntity.class.getDeclaredMethod("getId");

        interceptor.intercept(null, method, null, null);

        verify(entityMap).setLastMethod(new MethodContainer(method));
    }
}
