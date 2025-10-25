package com.amit.myblog.post.model;

import java.util.Objects;

public final class Post {

    private Long id;

    private String title;

    private String text;

    private long likesCount;

    private long commentsCount;

    public Post(String title, String text) {
        this.title = title;
        this.text = text;
    }

    public Post(Long id, String title, String text) {
        this.id = id;
        this.title = title;
        this.text = text;
    }

    public Post(Long id, String title, String text, long likesCount, long commentsCount) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.likesCount = likesCount;
        this.commentsCount = commentsCount;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getLikesCount() {
        return this.likesCount;
    }

    public void setLikesCount(long likesCount) {
        this.likesCount = likesCount;
    }

    public long getCommentsCount() {
        return this.commentsCount;
    }

    public void setCommentsCount(long commentsCount) {
        this.commentsCount = commentsCount;
    }

    @Override
    public boolean equals(Object otherObject) {
        if (otherObject == null || getClass() != otherObject.getClass()) {
            return false;
        }
        Post otherPost = (Post) otherObject;
        return Objects.equals(this.id, otherPost.id)
                && this.likesCount == otherPost.likesCount
                && this.commentsCount == otherPost.commentsCount
                && Objects.equals(this.title, otherPost.title)
                && Objects.equals(this.text, otherPost.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.title, this.text, this.likesCount, this.commentsCount);
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + this.id +
                ", title='" + this.title + '\'' +
                ", text='" + this.text + '\'' +
                ", likesCount=" + this.likesCount +
                ", commentsCount=" + this.commentsCount +
                '}';
    }

}
