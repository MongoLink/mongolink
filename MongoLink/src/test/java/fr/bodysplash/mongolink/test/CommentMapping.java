package fr.bodysplash.mongolink.test;

import fr.bodysplash.mongolink.mapper.ClassMap;

public class CommentMapping extends ClassMap<Comment>{

    public CommentMapping() {
        super(Comment.class);
    }

    @Override
    protected void map() {
        property(element().getValue());
    }
}
