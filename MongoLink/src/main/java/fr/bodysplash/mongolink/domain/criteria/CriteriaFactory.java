package fr.bodysplash.mongolink.domain.criteria;

import fr.bodysplash.mongolink.domain.QueryExecutor;

public class CriteriaFactory {

    public Criteria create(QueryExecutor executor) {
        return new Criteria(executor);
    }
}
