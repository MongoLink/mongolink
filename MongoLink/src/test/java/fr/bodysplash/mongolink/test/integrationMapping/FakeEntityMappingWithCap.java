package fr.bodysplash.mongolink.test.integrationMapping;

import fr.bodysplash.mongolink.domain.mapper.EntityMap;
import fr.bodysplash.mongolink.test.entity.FakeEntityWithCap;

public class FakeEntityMappingWithCap extends EntityMap<FakeEntityWithCap> {

    public FakeEntityMappingWithCap() {
        super(FakeEntityWithCap.class);
    }

    @Override
    public void map() {
        setCapped(true, 1048076, 50);
    }
}
