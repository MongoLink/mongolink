package fr.bodysplash.mongolink.test.simpleMapping;

import fr.bodysplash.mongolink.mapper.ClassMap;
import fr.bodysplash.mongolink.test.entity.FakeEntityWithNaturalId;

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
