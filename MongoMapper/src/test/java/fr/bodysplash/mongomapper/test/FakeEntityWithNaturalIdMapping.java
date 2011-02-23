package fr.bodysplash.mongomapper.test;

import fr.bodysplash.mongomapper.mapper.ClassMap;
import fr.bodysplash.mongomapper.mapper.IdGeneration;

public class FakeEntityWithNaturalIdMapping extends ClassMap<FakeEntityWithNaturalId> {
    public FakeEntityWithNaturalIdMapping() {
        super(FakeEntityWithNaturalId.class);
    }

    @Override
    protected void map() {
        id(IdGeneration.Natural).getNaturalKey();
    }
}
