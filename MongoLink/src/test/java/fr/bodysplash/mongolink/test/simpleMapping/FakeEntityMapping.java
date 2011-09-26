package fr.bodysplash.mongolink.test.simpleMapping;

import fr.bodysplash.mongolink.domain.mapper.ClassMap;
import fr.bodysplash.mongolink.test.entity.FakeEntity;

public class FakeEntityMapping extends ClassMap<FakeEntity> {

    public FakeEntityMapping() {
        super(FakeEntity.class);
    }

    @Override
    public void map() {
        id(element().getId());
        property(element().getValue());
        collection(element().getComments());
    }
}
