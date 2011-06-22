package fr.bodysplash.mongolink.test;

public class FakeEntity {

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getId() {
        return id;
    }

    private int value;
    private String id;
}
