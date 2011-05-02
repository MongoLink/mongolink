package fr.bodysplash.mongolink.test.inheritanceMapping;

import fr.bodysplash.mongolink.mapper.SubclassMap;
import fr.bodysplash.mongolink.test.entity.FakeChildEntity;

public class SubclassMapping extends SubclassMap<FakeChildEntity>{

    public SubclassMapping() {
        super(FakeChildEntity.class);
    }

    @Override
    protected void map() {

    }
}
