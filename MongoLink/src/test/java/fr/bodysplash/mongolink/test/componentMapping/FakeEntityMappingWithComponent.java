package fr.bodysplash.mongolink.test.componentMapping;

import fr.bodysplash.mongolink.domain.mapper.EntityMap;
import fr.bodysplash.mongolink.domain.mapper.EntityMapper;
import fr.bodysplash.mongolink.test.entity.FakeEntity;

public class FakeEntityMappingWithComponent extends EntityMap<FakeEntity> {

    public FakeEntityMappingWithComponent(EntityMapper<FakeEntity> mockMapper) {
        super(FakeEntity.class);
        innerMap = mockMapper;
    }

    @Override
    protected void map() {
        property(element().getComment());
    }

    @Override
    protected EntityMapper<FakeEntity> getMapper() {
        return innerMap;
    }

    private EntityMapper<FakeEntity> innerMap;
}
