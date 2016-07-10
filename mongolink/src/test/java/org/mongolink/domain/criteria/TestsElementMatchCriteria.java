package org.mongolink.domain.criteria;

import org.bson.Document;
import org.junit.Test;
import org.mongolink.domain.query.QueryExecutor;

import static org.fest.assertions.Assertions.*;
import static org.mockito.Mockito.*;

public class TestsElementMatchCriteria {

    @Test
    public void canGenerateQuery() {
        final Criteria criteria = new Criteria(mock(QueryExecutor.class));
        criteria.add(Restrictions.elementMatch("fieldName").equals("test", "test").equals("test2", "test2"));

        final Document query = criteria.createQuery();

        assertThat(query.containsKey("fieldName")).isTrue();
        final Document doted = (Document) query.get("fieldName");
        assertThat(doted.containsKey("$elemMatch")).isTrue();
        final Document elementMatch = (Document) doted.get("$elemMatch");
        assertThat(elementMatch.containsKey("test")).isTrue();
        assertThat(elementMatch.containsKey("test2")).isTrue();

    }
}
