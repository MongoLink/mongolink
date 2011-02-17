package fr.bodysplash.mongomapper.test;

public class FakeEntityWithNaturalId {

    public FakeEntityWithNaturalId() {
    }

    public FakeEntityWithNaturalId(String naturalKey) {
        this.naturalKey = naturalKey;
    }

    public String getNaturalKey() {
        return naturalKey;
    }

    private String naturalKey;
}
