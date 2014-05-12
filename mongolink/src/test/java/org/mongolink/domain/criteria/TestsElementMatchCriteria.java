package org.mongolink.domain.criteria;

import com.mongodb.DBObject;
import org.junit.Test;
import org.mongolink.domain.query.QueryExecutor;

import static org.fest.assertions.Assertions.*;
import static org.mockito.Mockito.*;

public class TestsElementMatchCriteria {

    @Test
    public void canGenerateQuery() {
        final Criteria criteria = new Criteria(mock(QueryExecutor.class));
        criteria.add(Restrictions.elementMatch("fieldName").equals("test", "test").equals("test2", "test2"));

        final DBObject query = criteria.createQuery();

        assertThat(query.containsField("fieldName")).isTrue();
        final DBObject doted = (DBObject) query.get("fieldName");
        assertThat(doted.containsField("$elemMatch")).isTrue();
        final DBObject elementMatch = (DBObject) doted.get("$elemMatch");
        assertThat(elementMatch.containsField("test")).isTrue();
        assertThat(elementMatch.containsField("test2")).isTrue();

    }
}
