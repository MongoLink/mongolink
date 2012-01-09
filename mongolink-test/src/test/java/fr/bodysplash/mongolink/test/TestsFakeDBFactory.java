package fr.bodysplash.mongolink.test;

import com.mongodb.FakeDB;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class TestsFakeDBFactory {

    @Test
    public void canGetDb() {
        final FakeDBFactory factory = new FakeDBFactory();

        final FakeDB fakeDB = factory.get("db de test");

        assertThat(fakeDB, notNullValue());
    }

    @Test
    public void canGetTheSameDbTwice() {
        final FakeDBFactory factory = new FakeDBFactory();

        final FakeDB fakeDB = factory.get("db de test");

        assertThat(fakeDB, is(factory.get("db de test")));
    }
}
