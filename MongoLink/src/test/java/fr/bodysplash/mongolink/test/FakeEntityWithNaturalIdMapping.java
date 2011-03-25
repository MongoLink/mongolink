package fr.bodysplash.mongolink.test;

import fr.bodysplash.mongolink.mapper.ClassMap;

public class FakeEntityWithNaturalIdMapping extends ClassMap<FakeEntityWithNaturalId> {

    public FakeEntityWithNaturalIdMapping() {
        super(FakeEntityWithNaturalId.class);
    }

    @Override
    protected void map() {
        id(element().getNaturalKey()).natural();
        property(element().getValue());
    }
}
