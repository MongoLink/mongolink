package fr.bodysplash.mongomapper.test;

import fr.bodysplash.mongomapper.mapper.ContextBuilder;

public class ContextBuilderTestFactory {

    public ContextBuilder withFakeEntity() {
        return new ContextBuilder("fr.bodysplash.mongomapper.test");
    }
}
