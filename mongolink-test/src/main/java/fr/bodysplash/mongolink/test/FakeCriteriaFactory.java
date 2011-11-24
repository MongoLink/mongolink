package fr.bodysplash.mongolink.test;

import fr.bodysplash.mongolink.domain.QueryExecutor;
import fr.bodysplash.mongolink.domain.criteria.*;

public class FakeCriteriaFactory extends CriteriaFactory {

    @Override
    public Criteria create(QueryExecutor executor) {
        return new FakeCriteria(executor);
    }

}
