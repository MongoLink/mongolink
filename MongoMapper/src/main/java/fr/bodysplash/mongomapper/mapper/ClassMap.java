package fr.bodysplash.mongomapper.mapper;

import net.sf.cglib.core.DefaultGeneratorStrategy;
import net.sf.cglib.proxy.Enhancer;
import org.apache.log4j.Logger;

import java.lang.reflect.Method;

public abstract class ClassMap<T> {

    private enum ElementType {
        property, collection, id
    }

    private final Class<T> type;
    private ElementType elementType;
    private final T interceptor;
    private final Mapper<T> mapper;
    private IdGeneration lastStrategy;
    private static final Logger LOGGER = Logger.getLogger(ClassMap.class);

    protected ClassMap(Class<T> type) {
        mapper = new Mapper(type);
        this.type = type;
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(type);
        enhancer.setStrategy(new DefaultGeneratorStrategy());
        enhancer.setCallback(new PropertyInterceptor(this));
        interceptor = (T) enhancer.create();
        map();
    }

    protected abstract void map();

    protected T descriptor() {
        return interceptor;
    }

    public Class<T> getType() {
        return type;
    }

    protected T id(IdGeneration strategy) {
        lastStrategy = strategy;
        elementType = ElementType.id;
        return interceptor;
    }

    protected T property() {
        elementType = ElementType.property;
        return interceptor;
    }

    protected T collection() {
        elementType = ElementType.collection;
        return interceptor;
    }

    protected T id() {
        return id(IdGeneration.Auto);
    }

    void addProperty(String name, Method method) {
        PropertyMapper property = new PropertyMapper(name, method);
        mapper.addProperty(property);
    }

    void setId(String name, Method method) {
        IdMapper id = new IdMapper(name, method, lastStrategy);
        mapper.setId(id);
    }

    public void addCollection(String collectionName, Method method) {
        CollectionMapper collection = new CollectionMapper(collectionName, method);
        mapper.addCollection(collection);
    }

    public boolean isProperty() {
        return elementType == ElementType.property;
    }

    public boolean isCollection() {
        return elementType == ElementType.collection;
    }

    public boolean isId() {
        return elementType == ElementType.id;
    }


    public void buildMapper(MapperContext context) {
        context.addMapper(mapper);
    }
}
