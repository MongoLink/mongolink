package fr.bodysplash.mongolink.test.simpleMapping;

import fr.bodysplash.mongolink.domain.mapper.ClassMap;
import fr.bodysplash.mongolink.test.entity.FakeEntity;

public class FakeEntityMappingWithCap extends ClassMap<FakeEntity> {

    public FakeEntityMappingWithCap() {
        super(FakeEntity.class);
    }

    @Override
    public void map() {
        setCapped(true);
    }
}
