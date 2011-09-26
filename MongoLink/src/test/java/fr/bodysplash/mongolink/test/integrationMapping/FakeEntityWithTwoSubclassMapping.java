package fr.bodysplash.mongolink.test.integrationMapping;

import fr.bodysplash.mongolink.domain.mapper.*;
import fr.bodysplash.mongolink.test.entity.*;


public class FakeEntityWithTwoSubclassMapping extends ClassMap<FakeEntity> {

    public FakeEntityWithTwoSubclassMapping() {
        super(FakeEntity.class);
    }

    @Override
    public void map() {
        id(element().getId());
        property(element().getValue());
        property(element().getIndex());
        collection(element().getComments());
        subclass(new SubclassMap<FakeChildEntity>(FakeChildEntity.class) {

            @Override
            protected void map() {
                property(element().getChildName());
            }
        });
        subclass(new SubclassMap<OtherFakeChildEntity>(OtherFakeChildEntity.class) {
            @Override
            protected void map() {

            }
        });
    }

}
