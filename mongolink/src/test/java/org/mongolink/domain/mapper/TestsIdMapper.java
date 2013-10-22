package org.mongolink.domain.mapper;

import com.mongodb.BasicDBObject;
import org.junit.Test;
import org.mongolink.utils.FieldContainer;

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;

public class TestsIdMapper {

    @Test
    public void canSerializeUuid() {
        IdMapper mapper = new IdMapper(mock(FieldContainer.class), IdGeneration.Natural);
        final UUID id = UUID.randomUUID();

        final Object dbValue = mapper.convertToDbValue(id);

        assertThat((UUID) dbValue, is(id));
    }

    @Test
    public void canSaveANullId() {
        final FieldContainer fieldContainer = mock(FieldContainer.class);
        when(fieldContainer.value(any())).thenReturn(null);
        IdMapper mapper = new IdMapper(fieldContainer, IdGeneration.Auto);
        final BasicDBObject dbObject = new BasicDBObject();

        mapper.save(new Object(), dbObject);

        assertThat(dbObject.containsField("_id"), is(false));
    }
}
