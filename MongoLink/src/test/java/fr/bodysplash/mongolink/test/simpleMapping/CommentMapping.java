package fr.bodysplash.mongolink.test.simpleMapping;

import fr.bodysplash.mongolink.mapper.ComponentMap;
import fr.bodysplash.mongolink.test.entity.Comment;

public class CommentMapping extends ComponentMap<Comment> {

    public CommentMapping() {
        super(Comment.class);
    }

    @Override
    protected void map() {
        property(element().getValue());
    }
}
