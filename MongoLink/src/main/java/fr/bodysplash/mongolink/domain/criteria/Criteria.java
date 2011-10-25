package fr.bodysplash.mongolink.domain.criteria;

import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import fr.bodysplash.mongolink.domain.CursorParameter;
import fr.bodysplash.mongolink.domain.QueryExecutor;

import java.util.Collections;
import java.util.List;

public class Criteria<T> {

    public Criteria(QueryExecutor executor) {
        this.executor = executor;
    }
    public List<T> list() {
        return executor.execute(createQuery(), parameter);
    }

    public void add(Restriction restriction) {
        restrictions.add(restriction);
    }

    DBObject createQuery() {
        final BasicDBObject result = new BasicDBObject();
        for (Restriction restriction : restrictions) {
            restriction.apply(result);
        }
        return result;
    }

    protected List<Restriction> getRestrictions() {
        return Collections.unmodifiableList(restrictions);
    }

    protected QueryExecutor getExecutor() {
        return executor;
    }

    public void limit(int limit) {
        parameter = parameter.limit(limit);
    }

    public void skip(int skip) {
        parameter = parameter.skip(skip);
    }

    protected CursorParameter getCursorParameter() {
        return parameter;
    }

    private List<Restriction> restrictions = Lists.newArrayList();
    private QueryExecutor executor;
    private CursorParameter parameter = CursorParameter.empty();
}
