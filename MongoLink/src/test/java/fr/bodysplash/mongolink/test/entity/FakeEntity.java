package fr.bodysplash.mongolink.test.entity;


import com.google.common.collect.Lists;

import java.util.List;

public class FakeEntity {

    FakeEntity() {

    }

    public FakeEntity(String value) {
        this.value = value;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void addComment(String comment) {
        comments.add(new Comment(comment));
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public FakeEntity getOtherEntity() {
        return otherEntity;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    private int index;
    private String id;
    private String value;
    private final List<Comment> comments = Lists.newArrayList();
    private FakeEntity otherEntity;
    private Comment comment;
}
