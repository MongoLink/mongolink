package fr.bodysplash.mongolink.test;

import fr.bodysplash.mongolink.MongoSession;
import fr.bodysplash.mongolink.criteria.Criteria;
import fr.bodysplash.mongolink.criteria.Restrictions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.*;

public class TestsFakeCriteria {

    @Rule
    public FakePersistentContext fakePersistentContext = new FakePersistentContext("fr.bodysplash.mongolink.test.mapping");


    @Before
    public void setUp() throws Exception {
        session = fakePersistentContext.getSession();
    }

    @Test
    public void canSearchByEquality() {
        savedEntityWithValue(3);
        savedEntityWithValue(2);
        final Criteria criteria = session.createCriteria(FakeEntity.class);
        criteria.add(Restrictions.eq("value", 3));

        final List<FakeEntity> list = criteria.list();

        assertThat(list.size(), is(1));
        assertThat(list.get(0).getValue(), is(3));
    }

    @Test
    public void canSearchBetween() {
        savedEntityWithValue(4);
        savedEntityWithValue(10);
        final Criteria criteria = session.createCriteria(FakeEntity.class);
        criteria.add(Restrictions.between("value", 4, 5));

        final List<FakeEntity> list = criteria.list();

        assertThat(list.size(), is(1));
        assertThat(list.get(0).getValue(), is(4));
    }

    private void savedEntityWithValue(int value) {
        final FakeEntity element = new FakeEntity();
        element.setValue(value);
        session.save(element);
    }


    private MongoSession session;
}
