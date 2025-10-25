package com.amit.myblog.comment.service;

import com.amit.myblog.comment.model.Comment;

import java.util.List;

public interface CommentService {

    List<Comment> getAllCommentsByPostId(long postId);

    Comment getCommentByPostIdAndCommentId(long postId, long commentId);

    Comment addComment(long postId, Comment comment);

    Comment editComment(long postId, long commentId, Comment comment);

    void deleteCommentByPostIdAndCommentId(long postId, long commentId);

}
