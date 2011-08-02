package fr.bodysplash.mongolink.test.simpleMapping;

import fr.bodysplash.mongolink.domain.mapper.ClassMap;
import fr.bodysplash.mongolink.test.entity.OtherEntityWithNaturalId;

public class OtherEntityWithNaturalIdMapping extends ClassMap<OtherEntityWithNaturalId> {

    public OtherEntityWithNaturalIdMapping() {
        super(OtherEntityWithNaturalId.class);
    }

    @Override
    protected void map() {
        id(element().getNaturalKey()).natural();
    }
}
