package fr.bodysplash.mongolink.test.simpleMapping;

import fr.bodysplash.mongolink.domain.mapper.EntityMap;
import fr.bodysplash.mongolink.test.entity.FakeEntity;

public class FakeEntityMapping extends EntityMap<FakeEntity> {

    public FakeEntityMapping() {
        super(FakeEntity.class);
    }

    @Override
    public void map() {
        id(element().getId());
        property(element().getValue());
        collection(element().getComments());
        component(element().getComment());
    }
}
