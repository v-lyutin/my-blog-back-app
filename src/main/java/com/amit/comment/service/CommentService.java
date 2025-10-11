package com.amit.comment.service;

import com.amit.comment.model.Comment;

import java.util.List;

public interface CommentService {

    List<Comment> getAllByPostId(long postId);

    Comment getByPostIdAndId(long postId, long commentId);

    Comment create(long postId, Comment comment);

    Comment update(long postId, long commentId, Comment comment);

    void deleteByPostIdAndId(long postId, long commentId);

}
