package com.amit.myblog.comment.repository;

import com.amit.myblog.comment.model.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository {

    List<Comment> findAllByPostId(long postId);

    Optional<Comment> findByPostIdAndId(long postId, long commentId);

    Comment save(Comment comment);

    Optional<Comment> update(Comment comment);

    boolean deleteByPostIdAndId(long postId, long commentId);

}
