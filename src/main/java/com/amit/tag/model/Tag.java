package com.amit.tag.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Objects;

@Table(schema = "my_blog", value = "tags")
public final class Tag {

    @Id
    @Column(value = "id")
    private long id;

    @Column(value = "name")
    private String name;

    public Tag() {}

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object otherObject) {
        if (otherObject == null || getClass() != otherObject.getClass()) {
            return false;
        }
        Tag otherTag = (Tag) otherObject;
        return this.id == otherTag.id && Objects.equals(this.name, otherTag.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.name);
    }

    @Override
    public String toString() {
        return "Tag{" +
                "id=" + this.id +
                ", name='" + this.name + '\'' +
                '}';
    }

}
