package fr.bodysplash.mongolink.test.integrationMapping;

import fr.bodysplash.mongolink.domain.mapper.ClassMap;
import fr.bodysplash.mongolink.test.entity.FakeEntityWithCap;

public class FakeEntityMappingWithCap extends ClassMap<FakeEntityWithCap> {

    public FakeEntityMappingWithCap() {
        super(FakeEntityWithCap.class);
    }

    @Override
    public void map() {
        setCapped(true);
    }
}
