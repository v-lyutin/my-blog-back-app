package com.amit.comment.model;

import java.util.Objects;

public final class Comment {

    private Long id;

    private String text;

    private long postId;

    public Comment(String text, long postId) {
        this.id = null;
        this.text = text;
        this.postId = postId;
    }

    public Comment() {}

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getPostId() {
        return this.postId;
    }

    public void setPostId(long postId) {
        this.postId = postId;
    }

    @Override
    public boolean equals(Object otherObject) {
        if (otherObject == null || getClass() != otherObject.getClass()) {
            return false;
        }
        Comment otherComment = (Comment) otherObject;
        return Objects.equals(this.id, otherComment.id) && this.postId == otherComment.postId && Objects.equals(this.text, otherComment.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.text, this.postId);
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + this.id +
                ", text='" + this.text + '\'' +
                ", postId=" + this.postId +
                '}';
    }

}
