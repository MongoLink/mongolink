package fr.bodysplash.mongolink.domain.updateStategy;

import com.mongodb.BasicDBList;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class TestsListVisitor {

    @Before
    public void setUp() throws Exception {
        dbObjectDiff = mock(DbObjectDiff.class);
    }

    @Test
    public void canUnset() {
        BasicDBList origin = new BasicDBList();
        origin.add("prems");
        BasicDBList target = new BasicDBList();
        final ListVisitor visitor = new ListVisitor(dbObjectDiff, origin);

        visitor.visit(target);

        verify(dbObjectDiff).pushKey("0");
        verify(dbObjectDiff).addUnset();
        verify(dbObjectDiff).popKey();
    }

    @Test
    public void addPullIfUnset() {
        BasicDBList origin = new BasicDBList();
        origin.add("prems");
        BasicDBList target = new BasicDBList();
        final ListVisitor visitor = new ListVisitor(dbObjectDiff, origin);

        visitor.visit(target);

        verify(dbObjectDiff).addPull(null);
    }

    @Test
    public void doNotAddPullIdNoUnset() {
        BasicDBList origin = new BasicDBList();
        BasicDBList target = new BasicDBList();
        final ListVisitor visitor = new ListVisitor(dbObjectDiff, origin);

        visitor.visit(target);

        verify(dbObjectDiff, never()).addPull(any());
    }

    private DbObjectDiff dbObjectDiff;
}
