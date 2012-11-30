package org.mongolink.domain.criteria;

import org.junit.Test;
import org.mongolink.domain.QueryExecutor;

import static org.mockito.Mockito.mock;

public class TestsElementMatchCriteria {

    @Test
    public void canDoStuff() {
        final Criteria criteria = new Criteria(mock(QueryExecutor.class));

        criteria.add(Restrictions.equals("id", "test"));

    }
}
