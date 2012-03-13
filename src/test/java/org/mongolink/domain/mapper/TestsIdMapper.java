package org.mongolink.domain.mapper;

import org.junit.Test;
import org.mongolink.utils.MethodContainer;

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class TestsIdMapper {

    @Test
    public void canSerializeUuid() {
        IdMapper mapper = new IdMapper(mock(MethodContainer.class), IdGeneration.Natural);
        final UUID id = UUID.randomUUID();
        
        final Object dbValue = mapper.convertToDbValue(id);
        
        assertThat((UUID) dbValue, is(id));
    }

    @Test
    public void canLoadFromBinData() {
        //75b686db-e0b9-4e71-a469-4e33ab45030a
        // BinData(3,"cU654NuGtnUKA0WrM05ppA==")
    }
}
