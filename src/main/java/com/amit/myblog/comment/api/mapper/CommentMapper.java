package com.amit.myblog.comment.api.mapper;

import com.amit.myblog.comment.api.dto.request.CommentCreateRequest;
import com.amit.myblog.comment.api.dto.request.CommentUpdateRequest;
import com.amit.myblog.comment.api.dto.response.CommentResponse;
import com.amit.myblog.comment.model.Comment;

public interface CommentMapper {

    Comment toComment(CommentCreateRequest commentCreateRequest);

    Comment toComment(CommentUpdateRequest commentUpdateRequest);

    CommentResponse toCommentResponse(Comment comment);

}
