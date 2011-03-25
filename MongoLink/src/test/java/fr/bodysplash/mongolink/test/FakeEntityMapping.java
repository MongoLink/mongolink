package fr.bodysplash.mongolink.test;

import fr.bodysplash.mongolink.mapper.ClassMap;

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
