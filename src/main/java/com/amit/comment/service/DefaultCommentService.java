package com.amit.comment.service;

import com.amit.comment.model.Comment;
import com.amit.comment.repository.CommentRepository;
import com.amit.comment.service.exception.CommentNotFoundException;
import com.amit.comment.service.exception.InvalidCommentException;
import com.amit.post.service.PostCrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public final class DefaultCommentService implements CommentService {

    private final CommentRepository commentRepository;

    private final PostCrudService postCrudService;

    @Autowired
    public DefaultCommentService(CommentRepository commentRepository, PostCrudService postCrudService) {
        this.commentRepository = commentRepository;
        this.postCrudService = postCrudService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> getAllByPostId(long postId) {
        return this.commentRepository.findAllByPostId(postId);
    }

    @Override
    @Transactional(readOnly = true)
    public Comment getByPostIdAndId(long postId, long commentId) {
        return this.commentRepository.findByPostIdAndId(postId, commentId)
                .orElseThrow(() -> new CommentNotFoundException(
                        "Comment with ID %d for post with ID %d not found.".formatted(commentId, postId)
                ));
    }

    @Override
    @Transactional
    public Comment create(long postId, Comment comment) {
        if (comment == null) {
            throw new InvalidCommentException("Comment must not be null.");
        }
        if (postId != comment.getPostId()) {
            throw new IllegalArgumentException("IDs for post must not be different.");
        }
        this.postCrudService.ensurePostExists(postId);
        return this.commentRepository.create(comment);
    }

    @Override
    @Transactional
    public Comment update(long postId, long commentId, Comment comment) {
        if (comment == null) {
            throw new InvalidCommentException("Comment must not be null.");
        }
        if (postId != comment.getPostId()) {
            throw new IllegalArgumentException("IDs for post must not be different.");
        }
        if (commentId != comment.getId()) {
            throw new IllegalArgumentException("IDs for comment must not be different");
        }
        return this.commentRepository.update(comment)
                .orElseThrow(() -> new CommentNotFoundException(
                        "Comment with ID %d for post with ID %d not found.".formatted(comment.getId(), comment.getPostId())
                ));
    }

    @Override
    @Transactional
    public void deleteByPostIdAndId(long postId, long commentId) {
        boolean isDeleted = this.commentRepository.deleteByPostIdAndId(postId, commentId);
        if (!isDeleted) {
            throw new CommentNotFoundException(
                    "Comment with ID %d for post with ID %d not found.".formatted(commentId, postId)
            );
        }
    }

}
