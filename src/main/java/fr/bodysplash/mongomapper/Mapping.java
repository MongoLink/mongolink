package fr.bodysplash.mongomapper;

import net.sf.cglib.core.DefaultGeneratorStrategy;
import net.sf.cglib.proxy.Enhancer;
import org.apache.log4j.Logger;

import java.lang.reflect.Method;


public class Mapping<T> {


    private enum ElementType {
        property, collection, id;
    }

    private static final Logger LOGGER = Logger.getLogger(Mapping.class);

    private T interceptor;
    private Mapper<T> mapper;
    private ElementType elementType;

    public static <T> Mapping<T> createNew(Class<T> clazz) {
        return new Mapping(clazz);
    }


    private Mapping(Class<T> clazz) {
        mapper = new Mapper(clazz);
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setStrategy(new DefaultGeneratorStrategy());
        enhancer.setCallback(new PropertyInterceptor(this));
        interceptor = (T) enhancer.create();

    }

    public T property() {
        elementType = Mapping.ElementType.property;
        return interceptor;
    }

    public T collection() {
        elementType = Mapping.ElementType.collection;
        return interceptor;
    }

    public T id() {
        elementType = Mapping.ElementType.id;
        return interceptor;
    }

    void addProperty(String name, Method method) {
        PropertyMapper property = new PropertyMapper(name, method);
        mapper.addProperty(property);
    }

    public void setId(String name, Method method) {
        IdMapper id = new IdMapper(name, method);
        mapper.setId(id);
    }

    public void addCollection(String collectionName, Method method) {
        CollectionMapper collection = new CollectionMapper(collectionName, method);
        mapper.addCollection(collection);
    }

    public boolean isProperty() {
        return elementType == Mapping.ElementType.property;
    }

    public boolean isCollection() {
        return elementType == Mapping.ElementType.collection;
    }

    public boolean isId() {
        return elementType == Mapping.ElementType.id;
    }

    public Mapper<T> createMapper() {
        return mapper;
    }
}
