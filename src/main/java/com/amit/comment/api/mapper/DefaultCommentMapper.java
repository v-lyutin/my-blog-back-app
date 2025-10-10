package com.amit.comment.api.mapper;

import com.amit.comment.api.dto.request.CommentCreateRequest;
import com.amit.comment.api.dto.request.CommentUpdateRequest;
import com.amit.comment.api.dto.response.CommentResponse;
import com.amit.comment.model.Comment;
import org.springframework.stereotype.Component;

@Component
public final class DefaultCommentMapper implements CommentMapper {

    @Override
    public Comment toComment(CommentCreateRequest commentCreateRequest) {
        return new Comment(commentCreateRequest.text(), commentCreateRequest.postId());
    }

    @Override
    public Comment toComment(CommentUpdateRequest commentUpdateRequest) {
        return new Comment(commentUpdateRequest.id(), commentUpdateRequest.text(), commentUpdateRequest.postId());
    }

    @Override
    public CommentResponse toCommentResponse(Comment comment) {
        return new CommentResponse(comment.getId(), comment.getText(), comment.getPostId());
    }

}
