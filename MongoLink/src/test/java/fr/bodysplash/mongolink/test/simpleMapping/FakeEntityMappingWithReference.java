package fr.bodysplash.mongolink.test.simpleMapping;

import fr.bodysplash.mongolink.domain.mapper.EntityMap;
import fr.bodysplash.mongolink.domain.mapper.EntityMapper;
import fr.bodysplash.mongolink.test.entity.FakeEntity;

public class FakeEntityMappingWithReference extends EntityMap<FakeEntity> {

    public FakeEntityMappingWithReference() {
        super(FakeEntity.class);
        innerMapper = new EntityMapper<FakeEntity>(FakeEntity.class);
    }

    public FakeEntityMappingWithReference(EntityMapper<FakeEntity> mapper) {
        super(FakeEntity.class);
        this.innerMapper = mapper;
    }

    @Override
    protected void map() {
        References(element().getOtherEntity());
    }

    @Override
    protected EntityMapper<FakeEntity> getMapper() {
        return innerMapper;
    }

    private EntityMapper<FakeEntity> innerMapper;
}
