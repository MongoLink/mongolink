package fr.bodysplash.mongolink.test.factory;

import fr.bodysplash.mongolink.domain.mapper.ContextBuilder;

public class ContextBuilderTestFactory {

    public ContextBuilder withFakeEntity() {
        return new ContextBuilder("fr.bodysplash.mongolink.test.simpleMapping");
    }
}
