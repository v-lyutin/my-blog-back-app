package com.amit.post.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Objects;
import java.util.Set;

@Table(schema = "my_blog", value = "posts")
public final class Post {

    @Id
    @Column(value = "id")
    private Long id;

    @Column(value = "title")
    private String title;

    @Column(value = "text")
    private String text;

    @MappedCollection(idColumn = "post_id")
    private Set<PostTagReference> tags;

    @Column(value = "likes_count")
    private long likesCount;

    @Column(value = "comments_count")
    private long commentsCount;

    public Post() {}

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
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

    public Set<PostTagReference> getTags() {
        return this.tags;
    }

    public void setTags(Set<PostTagReference> tags) {
        this.tags = tags;
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
                && Objects.equals(this.text, otherPost.text)
                && Objects.equals(this.tags, otherPost.tags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.title, this.text, this.tags, this.likesCount, this.commentsCount);
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + this.id +
                ", title='" + this.title + '\'' +
                ", text='" + this.text + '\'' +
                ", tags=" + this.tags +
                ", likesCount=" + this.likesCount +
                ", commentsCount=" + this.commentsCount +
                '}';
    }

}
