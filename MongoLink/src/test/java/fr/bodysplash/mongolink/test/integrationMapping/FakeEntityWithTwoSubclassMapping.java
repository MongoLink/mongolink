package fr.bodysplash.mongolink.test.integrationMapping;

import fr.bodysplash.mongolink.mapper.ClassMap;
import fr.bodysplash.mongolink.mapper.SubclassMap;
import fr.bodysplash.mongolink.test.entity.FakeChildEntity;
import fr.bodysplash.mongolink.test.entity.FakeEntity;
import fr.bodysplash.mongolink.test.entity.OtherFakeChildEntity;


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
        subclass(new SubclassMap<FakeChildEntity>(FakeChildEntity.class){

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
