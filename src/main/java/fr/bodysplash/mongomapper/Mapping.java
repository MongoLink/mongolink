package fr.bodysplash.mongomapper;

import net.sf.cglib.core.DefaultGeneratorStrategy;
import net.sf.cglib.proxy.Enhancer;
import org.apache.log4j.Logger;

import java.lang.reflect.Method;


public class Mapping<T> {

    private static final Logger LOGGER = Logger.getLogger(Mapping.class);
    private T interceptor;
    private boolean property;
    private boolean collection;
    private Mapper<T> mapper;


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
        property = true;
        collection = false;
        return interceptor;
    }

    public T collection() {
        property = false;
        collection = true;
        return interceptor;
    }

    void addProperty(String name, Method method) {
        PropertyMapper property = new PropertyMapper(name, method);
        mapper.addProperty(property);
    }

    public void addCollection(String collectionName, Method method) {
        CollectionMapper collection = new CollectionMapper(collectionName, method);
        mapper.addCollection(collection);
    }

    public boolean isProperty() {
        return property;
    }

    public boolean isCollection() {
        return collection;
    }

    public Mapper<T> createMapper() {
        return mapper;
    }
}
