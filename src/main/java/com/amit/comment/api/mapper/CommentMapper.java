package com.amit.comment.api.mapper;

import com.amit.comment.api.dto.request.CommentCreateRequest;
import com.amit.comment.api.dto.request.CommentUpdateRequest;
import com.amit.comment.api.dto.response.CommentResponse;
import com.amit.comment.model.Comment;

public interface CommentMapper {

    Comment toComment(CommentCreateRequest commentCreateRequest);

    Comment toComment(CommentUpdateRequest commentUpdateRequest);

    CommentResponse toCommentResponse(Comment comment);

}
