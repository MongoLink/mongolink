package fr.bodysplash.mongolink.test.inheritanceMapping;

import fr.bodysplash.mongolink.domain.mapper.EntityMap;
import fr.bodysplash.mongolink.domain.mapper.SubclassMap;
import fr.bodysplash.mongolink.test.entity.FakeChildEntity;
import fr.bodysplash.mongolink.test.entity.FakeEntity;


public class FakeEntityWithSubclassMapping extends EntityMap<FakeEntity> {

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
