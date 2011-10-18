package fr.bodysplash.mongolink.test.simpleMapping;

import fr.bodysplash.mongolink.domain.mapper.EntityMap;
import fr.bodysplash.mongolink.test.entity.OtherEntityWithNaturalId;

public class OtherEntityWithNaturalIdMapping extends EntityMap<OtherEntityWithNaturalId> {

    public OtherEntityWithNaturalIdMapping() {
        super(OtherEntityWithNaturalId.class);
    }

    @Override
    protected void map() {
        id(element().getNaturalKey()).natural();
    }
}
