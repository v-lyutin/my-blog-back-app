package com.amit.comment.repository;

import com.amit.comment.model.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository {

    List<Comment> findAllByPostId(long postId);

    Optional<Comment> findByPostIdAndId(long postId, long commentId);

    Comment create(Comment comment);

    Optional<Comment> update(Comment comment);

    boolean deleteByPostIdAndId(long postId, long commentId);

}
