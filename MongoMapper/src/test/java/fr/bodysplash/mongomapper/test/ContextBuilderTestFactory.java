package fr.bodysplash.mongomapper.test;

import fr.bodysplash.mongomapper.mapper.ContextBuilder;
import fr.bodysplash.mongomapper.mapper.Mapping;

public class ContextBuilderTestFactory {

    public ContextBuilder withFakeEntity() {
        ContextBuilder result = new ContextBuilder();
        Mapping<FakeEntity> mapping = result.newMapping(FakeEntity.class);
        mapping.id().getId();
        mapping.property().getValue();
        return result;
    }
}
