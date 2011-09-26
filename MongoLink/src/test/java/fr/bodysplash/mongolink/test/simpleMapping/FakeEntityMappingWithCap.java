package fr.bodysplash.mongolink.test.simpleMapping;

import fr.bodysplash.mongolink.domain.mapper.ClassMap;
import fr.bodysplash.mongolink.test.entity.*;

public class FakeEntityMappingWithCap extends ClassMap<FakeEntityWithCap> {

    public FakeEntityMappingWithCap() {
        super(FakeEntityWithCap.class);
    }

    @Override
    public void map() {
        setCapped(true, 1048076, 50);
    }
}
