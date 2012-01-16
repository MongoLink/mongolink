package fr.bodysplash.mongolink.test.integrationMapping;

import fr.bodysplash.mongolink.domain.mapper.EntityMap;
import fr.bodysplash.mongolink.domain.mapper.SubclassMap;
import fr.bodysplash.mongolink.test.entity.FakeChildEntity;
import fr.bodysplash.mongolink.test.entity.FakeEntity;
import fr.bodysplash.mongolink.test.entity.OtherFakeChildEntity;


public class FakeEntityWithTwoSubclassMapping extends EntityMap<FakeEntity> {

    public FakeEntityWithTwoSubclassMapping() {
        super(FakeEntity.class);
    }

    @Override
    public void map() {
        id(element().getId());
        property(element().getValue());
        property(element().getIndex());
        collection(element().getComments());
        component(element().getComment());
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
