package org.mongolink.domain.updateStrategy;

import com.mongodb.BasicDBList;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class TestsListVisitor {

    @Before
    public void setUp() throws Exception {
        dbObjectDiff = mock(DbObjectDiff.class);
    }

    @Test
    public void canUnset() {
        diff(listWith("prems"), listWith());

        verify(dbObjectDiff).pushKey("0");
        verify(dbObjectDiff).addUnset();
        verify(dbObjectDiff).popKey();
    }

    @Test
    public void addPullIfUnset() {
        diff(listWith("prems"), listWith());

        verify(dbObjectDiff).addPull(null);
    }

    @Test
    public void canHandleListWithSameValues() {
        diff(listWith("prems", "prems"), listWith("prems"));

        verify(dbObjectDiff).pushKey("1");
        verify(dbObjectDiff).addUnset();
    }

    @Test
    public void canRemoveListWithSameValuesAndDifferentOne() {
        diff(listWith("prems", "prems", "encore"), listWith("prems", "encore"));
        verify(dbObjectDiff).pushKey("1");
        verify(dbObjectDiff).addUnset();
        reset(dbObjectDiff);

        diff(listWith("encore", "prems", "autre"), listWith("encore", "autre"));
        verify(dbObjectDiff).pushKey("1");
        verify(dbObjectDiff).addUnset();
        reset(dbObjectDiff);
    }

    @Test
    public void canGenerateUpdateOnComponent() {
        diff(listWith("prems"), listWith("deuz"));
        verify(dbObjectDiff).pushKey("0");
        verify(dbObjectDiff).addSet("deuz");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void canPushAllOnMultipleAdd() {
        diff(listWith(), listWith("1", "2"));
        ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
        verify(dbObjectDiff).addPushAll(captor.capture());
        List<Object> value = captor.getValue();
        assertThat(value).hasSize(2).contains("1", "2");
    }

    private BasicDBList listWith(final Object... values) {
        final BasicDBList result = new BasicDBList();
        Collections.addAll(result, values);
        return result;
    }

    private void diff(final BasicDBList origin, final BasicDBList target) {
        final ListVisitor visitor = new ListVisitor(dbObjectDiff, origin);

        visitor.visit(target);
    }

    private DbObjectDiff dbObjectDiff;
}
