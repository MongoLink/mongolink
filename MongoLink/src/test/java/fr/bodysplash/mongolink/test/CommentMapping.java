package fr.bodysplash.mongolink.test;

import fr.bodysplash.mongolink.mapper.ComponentMap;

public class CommentMapping extends ComponentMap<Comment> {

    public CommentMapping() {
        super(Comment.class);
    }

    @Override
    protected void map() {
        property(element().getValue());
    }
}
