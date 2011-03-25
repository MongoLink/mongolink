package fr.bodysplash.mongolink.mapper;

import net.sf.cglib.core.DefaultGeneratorStrategy;
import net.sf.cglib.proxy.Enhancer;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.lang.reflect.Method;

public abstract class ClassMap<T> {

    private static final Logger LOGGER = Logger.getLogger(ClassMap.class);
    private Method lastMethod;
    private final Class<T> type;
    private final T interceptor;
    private final Mapper<T> mapper;

    protected ClassMap(Class<T> type) {
        mapper = new Mapper(type);
        this.type = type;
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(type);
        enhancer.setStrategy(new DefaultGeneratorStrategy());
        enhancer.setCallback(new PropertyInterceptor(this));
        interceptor = (T) enhancer.create();
        LOGGER.debug("Mapping " + getType());
        map();
    }

    public void setLastMethod(Method lastMethod) {
        this.lastMethod = lastMethod;
    }

    protected abstract void map();

    protected T element() {
        return interceptor;
    }

    public Class<T> getType() {
        return type;
    }

    protected void property(Object value) {
        String name = methodName();
        LOGGER.debug("Mapping property " + name);
        PropertyMapper property = new PropertyMapper(name, lastMethod);
        mapper.addProperty(property);
    }

    protected void collection(Object value) {
        String collectionName = methodName();
        LOGGER.debug("Mapping collection:" + collectionName);
        CollectionMapper collection = new CollectionMapper(collectionName, lastMethod);
        mapper.addCollection(collection);
    }

    protected IdMapper id(Object value) {
        String methodName = methodName();
        LOGGER.debug("Mapping id " + methodName);
        IdMapper id = new IdMapper(methodName, lastMethod, IdGeneration.Auto);
        mapper.setId(id);
        return id;
    }

    public void buildMapper(MapperContext context) {
        context.addMapper(mapper);
    }

    private String methodName() {
        return StringUtils.uncapitalize(lastMethod.getName().substring(3, lastMethod.getName().length()));
    }
}
