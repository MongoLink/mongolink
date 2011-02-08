package fr.bodysplash.mongomapper.test;


import com.google.common.collect.Lists;

import java.util.List;

public class Entity {

    private String id;
    private String value;
    private List<Comment> comments = Lists.newArrayList();

    Entity() {
        
    }

    public Entity(String value) {
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
}
