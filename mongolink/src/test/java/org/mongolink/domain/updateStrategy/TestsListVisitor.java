package org.mongolink.domain.updateStrategy;

import com.mongodb.BasicDBList;
import org.junit.*;
import org.mockito.*;

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
    public void addPullIfUnset() {
        diff(listWith("prems"), listWith());

        verify(dbObjectDiff).addPull("prems");
    }

    @Test
    @Ignore("TODO: think about how to deal with this limitation")
    public void canHandleListWithSameValues() {
        diff(listWith("prems", "prems"), listWith("prems"));

        verify(dbObjectDiff).pushKey("1");
        verify(dbObjectDiff).addUnset();
    }

    @Test
    @Ignore("TODO: think about how to deal with this limitation")
    public void canRemoveListWithSameValuesAndDifferentOne() {
        diff(listWith("prems", "prems", "encore"), listWith("prems", "encore"));
        final InOrder inOrder = inOrder(dbObjectDiff);
        inOrder.verify(dbObjectDiff).pushKey("1");
        inOrder.verify(dbObjectDiff).addSet("prems");
        inOrder.verify(dbObjectDiff).popKey();
        reset(dbObjectDiff);

        diff(listWith("encore", "prems", "autre"), listWith("encore", "autre"));
        verify(dbObjectDiff).pushKey("1");
        reset(dbObjectDiff);
    }

    @Test
    public void canGenerateUpdateOnComponent() {
        diff(listWith("prems"), listWith("deuz"));
        verify(dbObjectDiff).pushKey("0");
        verify(dbObjectDiff).addSet("deuz");
        verify(dbObjectDiff).popKey();
    }

    @Test
    public void generatesOnlyAPullOnDeletion() {
        diff(listWith("prems", "deus"), listWith("deus"));

        verify(dbObjectDiff).addPull("prems");
        verify(dbObjectDiff, times(0)).addSet(any());
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
