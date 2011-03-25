package fr.bodysplash.mongolink.test;

import fr.bodysplash.mongolink.mapper.ContextBuilder;

public class ContextBuilderTestFactory {

    public ContextBuilder withFakeEntity() {
        return new ContextBuilder("fr.bodysplash.mongolink.test");
    }
}
