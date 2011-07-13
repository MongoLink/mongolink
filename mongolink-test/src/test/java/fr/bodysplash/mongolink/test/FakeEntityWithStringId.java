package fr.bodysplash.mongolink.test;

public class FakeEntityWithStringId {

    public FakeEntityWithStringId() {
    }

    public FakeEntityWithStringId(String uri) {
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }

    private String uri;
}
