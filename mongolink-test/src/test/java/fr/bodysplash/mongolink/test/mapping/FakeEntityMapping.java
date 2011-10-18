package fr.bodysplash.mongolink.test.mapping;

import fr.bodysplash.mongolink.domain.mapper.EntityMap;
import fr.bodysplash.mongolink.test.FakeEntity;

public class FakeEntityMapping extends EntityMap<FakeEntity> {

    public FakeEntityMapping() {
        super(FakeEntity.class);
    }

    @Override
    protected void map() {
        id(element().getId()).auto();
        property(element().getValue());
    }
}
