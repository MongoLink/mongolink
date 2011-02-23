package fr.bodysplash.mongomapper.test;

import fr.bodysplash.mongomapper.mapper.ClassMap;

public class CommentMapping extends ClassMap<Comment>{

    public CommentMapping() {
        super(Comment.class);
    }

    @Override
    protected void map() {
        property().getValue();
    }
}
