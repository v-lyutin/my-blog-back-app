package com.amit.tag.model;

import java.util.Objects;

public final class Tag {

    private Long id;

    private String name;

    public Tag() {}

    public Tag(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
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
        return Objects.equals(this.id, otherTag.id) && Objects.equals(this.name, otherTag.name);
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
