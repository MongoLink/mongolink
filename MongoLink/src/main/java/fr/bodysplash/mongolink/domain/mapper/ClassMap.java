package fr.bodysplash.mongolink.domain.mapper;

import fr.bodysplash.mongolink.utils.MethodContainer;
import net.sf.cglib.core.DefaultGeneratorStrategy;
import net.sf.cglib.proxy.Enhancer;
import org.apache.log4j.Logger;

@SuppressWarnings("unchecked")
public abstract class ClassMap<T> {

    public ClassMap(Class<T> type) {
        this.type = type;
        LOGGER.debug("Mapping " + getType());
        interceptor = createInterceptor(type);
    }

    protected T createInterceptor(Class<T> type) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(type);
        enhancer.setStrategy(new DefaultGeneratorStrategy());
        enhancer.setCallback(new PropertyInterceptor(this));
        return (T) enhancer.create();
    }

    public void setLastMethod(MethodContainer lastMethod) {
        this.lastMethod = lastMethod;
    }

    protected T element() {
        return interceptor;
    }

    public Class<T> getType() {
        return type;
    }

    protected void property(Object value) {
        String name = lastMethod.shortName();
        LOGGER.debug("Mapping property " + name);
        getMapper().addProperty(new PropertyMapper(lastMethod));
    }

    protected void collection(Object value) {
        LOGGER.debug("Mapping collection:" + lastMethod.shortName());
        getMapper().addCollection(new CollectionMapper(lastMethod));
    }

    public void buildMapper(MapperContext context) {
        map();
        context.addMapper(getMapper());
    }

    protected abstract void map();

    protected MethodContainer getLastMethod() {
        return lastMethod;
    }

    protected void setCapped(boolean value, int cappedSize, int cappedMax) {
        getMapper().setCapped(value, cappedSize, cappedMax);
    }

    protected abstract ClassMapper<T> getMapper();

    private static final Logger LOGGER = Logger.getLogger(EntityMap.class);
    private MethodContainer lastMethod;
    private final Class<T> type;
    private final T interceptor;
}
