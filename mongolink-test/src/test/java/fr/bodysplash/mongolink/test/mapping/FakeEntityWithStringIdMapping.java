package fr.bodysplash.mongolink.test.mapping;

import fr.bodysplash.mongolink.domain.mapper.EntityMap;
import fr.bodysplash.mongolink.test.FakeEntityWithStringId;


public class FakeEntityWithStringIdMapping extends EntityMap<FakeEntityWithStringId> {

    public FakeEntityWithStringIdMapping() {
        super(FakeEntityWithStringId.class);
    }

    @Override
    protected void map() {
        id(element().getUri()).natural();
    }
}
