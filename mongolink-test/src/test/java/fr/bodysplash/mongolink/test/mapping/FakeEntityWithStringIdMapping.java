package fr.bodysplash.mongolink.test.mapping;

import fr.bodysplash.mongolink.mapper.ClassMap;
import fr.bodysplash.mongolink.test.FakeEntityWithStringId;


public class FakeEntityWithStringIdMapping extends ClassMap<FakeEntityWithStringId> {

    public FakeEntityWithStringIdMapping() {
        super(FakeEntityWithStringId.class);
    }

    @Override
    protected void map() {
        id(element().getUri()).natural();
    }
}
