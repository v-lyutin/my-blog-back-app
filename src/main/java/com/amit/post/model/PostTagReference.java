package com.amit.post.model;

import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Objects;

@Table(schema = "my_blog", value = "post_tag")
public final class PostTagReference {

    @Column("tag_id")
    private long tagId;

    public PostTagReference(long tagId) {
        this.tagId = tagId;
    }

    public PostTagReference() {}

    public long getTagId() {
        return this.tagId;
    }

    public void setTagId(long tagId) {
        this.tagId = tagId;
    }

    @Override
    public boolean equals(Object otherObject) {
        if (otherObject == null || getClass() != otherObject.getClass()) {
            return false;
        }
        PostTagReference otherPostTagReference = (PostTagReference) otherObject;
        return this.tagId == otherPostTagReference.tagId;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.tagId);
    }

    @Override
    public String toString() {
        return "PostTagReference{" +
                "tagId=" + this.tagId +
                '}';
    }

}
