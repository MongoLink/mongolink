package fr.bodysplash.mongolink.test.entity;

public class OtherEntityWithNaturalId {

    public OtherEntityWithNaturalId() {
    }

    public OtherEntityWithNaturalId(String naturalKey) {
        this.naturalKey = naturalKey;
    }

    public String getNaturalKey() {
        return naturalKey;
    }

    private String naturalKey;


}
