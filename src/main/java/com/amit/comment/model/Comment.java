package com.amit.comment.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Objects;

@Table(schema = "my_blog", value = "comments")
public final class Comment {

    @Id
    @Column(value = "id")
    private Long id;

    @Column(value = "text")
    private String text;

    @Column(value = "post_id")
    private long postId;

    public Comment() {}

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
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
        return this.id == otherComment.id && this.postId == otherComment.postId && Objects.equals(this.text, otherComment.text);
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
