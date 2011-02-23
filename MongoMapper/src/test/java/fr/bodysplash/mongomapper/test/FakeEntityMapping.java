package fr.bodysplash.mongomapper.test;

import fr.bodysplash.mongomapper.mapper.ClassMap;

public class FakeEntityMapping extends ClassMap<FakeEntity> {

    public FakeEntityMapping() {
        super(FakeEntity.class);
    }

    @Override
    public void map() {
        id().getId();
        property().getValue();
        collection().getComments();
    }

}
