package fr.bodysplash.mongomapper.test;

import fr.bodysplash.mongomapper.mapper.ClassMap;

public class FakeEntityWithNaturalIdMapping extends ClassMap<FakeEntityWithNaturalId> {

    public FakeEntityWithNaturalIdMapping() {
        super(FakeEntityWithNaturalId.class);
    }

    @Override
    protected void map() {
        id(element().getNaturalKey()).natural();
    }
}
