package fr.bodysplash.mongolink.test.mapping;

import fr.bodysplash.mongolink.mapper.ClassMap;
import fr.bodysplash.mongolink.test.FakeEntity;

public class FakeEntityMapping extends ClassMap<FakeEntity>{
    
    public FakeEntityMapping() {
        super(FakeEntity.class);
    }

    @Override
    protected void map() {
        id(element().getId()).auto();
        property(element().getValue());
    }
}
