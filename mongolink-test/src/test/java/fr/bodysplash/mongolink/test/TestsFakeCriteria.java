package fr.bodysplash.mongolink.test;

import fr.bodysplash.mongolink.MongoSession;
import fr.bodysplash.mongolink.domain.criteria.*;
import org.junit.*;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

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
        criteria.add(Restrictions.equals("value", 3));

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

    @Test
    public void canSearchByEqualityOnId() {
        final String uri = "uri";
        final FakeEntityWithStringId entity = new FakeEntityWithStringId(uri);
        session.save(entity);
        final Criteria criteria = session.createCriteria(FakeEntityWithStringId.class);
        criteria.add(Restrictions.equals("_id", uri));

        final List<FakeEntityWithStringId> list = criteria.list();

        assertThat(list.size(), is(1));
        assertThat(list.get(0).getUri(), is(uri));
    }

    @Test
    public void canLimit() {
        savedEntityWithValue(4);
        savedEntityWithValue(10);
        final Criteria criteria = session.createCriteria(FakeEntity.class);
        criteria.limit(1);

        final List<FakeEntity> list = criteria.list();

        assertThat(list.size(), is(1));
        assertThat(list.get(0).getValue(), is(4));
    }

    @Test
    public void canSkip() {
        savedEntityWithValue(4);
        savedEntityWithValue(10);
        final Criteria criteria = session.createCriteria(FakeEntity.class);
        criteria.skip(1);

        final List<FakeEntity> list = criteria.list();

        assertThat(list.size(), is(1));
        assertThat(list.get(0).getValue(), is(10));
    }

    @Test
    public void canLimitAndSkip() {
        savedEntityWithValue(1);
        savedEntityWithValue(2);
        savedEntityWithValue(3);
        savedEntityWithValue(4);
        final Criteria criteria = session.createCriteria(FakeEntity.class);

        criteria.skip(1);
        criteria.limit(2);

        final List<FakeEntity> list = criteria.list();

        assertThat(list.size(), is(2));
        assertThat(list.get(0).getValue(), is(2));
        assertThat(list.get(1).getValue(), is(3));
    }

    @Test
    public void canSearchByInequality() {
        savedEntityWithValue(3);
        savedEntityWithValue(2);
        final Criteria criteria = session.createCriteria(FakeEntity.class);
        criteria.add(Restrictions.notEquals("value", 3));

        final List<FakeEntity> list = criteria.list();

        assertThat(list.size(), is(1));
        assertThat(list.get(0).getValue(), is(2));
    }

    private void savedEntityWithValue(int value) {
        final FakeEntity element = new FakeEntity();
        element.setValue(value);
        session.save(element);
    }

    private MongoSession session;
}
