package fr.bodysplash.mongolink.test.simpleMapping;

import fr.bodysplash.mongolink.domain.mapper.EntityMap;
import fr.bodysplash.mongolink.test.entity.FakeEntityWithNaturalId;

public class FakeEntityWithNaturalIdMapping extends EntityMap<FakeEntityWithNaturalId> {

    public FakeEntityWithNaturalIdMapping() {
        super(FakeEntityWithNaturalId.class);
    }

    @Override
    protected void map() {
        id(element().getNaturalKey()).natural();
        property(element().getValue());
    }
}
