package fr.bodysplash.mongolink.test.inheritanceMapping;

import fr.bodysplash.mongolink.domain.mapper.*;
import fr.bodysplash.mongolink.test.entity.*;


public class FakeEntityWithSubclassMapping extends ClassMap<FakeEntity> {

    public FakeEntityWithSubclassMapping() {
        super(FakeEntity.class);
    }

    @Override
    public void map() {
        id(element().getId()).natural();
        property(element().getValue());
        subclass(new SubclassMap<FakeChildEntity>(FakeChildEntity.class) {

            @Override
            protected void map() {
                property(element().getChildName());
            }
        });
    }

}
