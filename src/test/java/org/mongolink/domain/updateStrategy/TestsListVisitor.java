package org.mongolink.domain.updateStrategy;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

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

    @Test
    public void canGenerateUpdateOnSupDocument() {
        BasicDBObject value1 = new BasicDBObject("test", "un");
        BasicDBObject value2 = new BasicDBObject("test", "deux");

        diff(listWith(value1), listWith(value2));

        verify(dbObjectDiff).pushKey("0");
        verify(dbObjectDiff).pushKey("test");
        verify(dbObjectDiff).addSet("deux");
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
