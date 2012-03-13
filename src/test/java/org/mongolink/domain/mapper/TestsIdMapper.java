package org.mongolink.domain.mapper;

import com.mongodb.BasicDBObject;
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
    public void canSaveANullId() {
        final MethodContainer methodContainer = mock(MethodContainer.class);
        when(methodContainer.invoke(any())).thenReturn(null);
        IdMapper mapper = new IdMapper(methodContainer, IdGeneration.Auto);
        final BasicDBObject dbObject = new BasicDBObject();
        
        mapper.save(new Object(), dbObject);
        
        assertThat(dbObject.containsField("_id"), is(false));
    }
}
