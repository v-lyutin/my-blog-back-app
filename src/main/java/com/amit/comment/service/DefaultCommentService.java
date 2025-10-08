package com.amit.comment.service;

import com.amit.comment.model.Comment;
import com.amit.comment.repository.CommentRepository;
import com.amit.comment.service.exception.CommentNotFoundException;
import com.amit.comment.service.exception.InvalidCommentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public final class DefaultCommentService implements CommentService {

    private final CommentRepository commentRepository;

    @Autowired
    public DefaultCommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
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
    public Comment create(Comment comment) {
        if (comment == null) {
            throw new InvalidCommentException("Comment must not be null.");
        }
        return this.commentRepository.create(comment);
    }

    @Override
    @Transactional
    public Comment update(Comment comment) {
        if (comment == null) {
            throw new InvalidCommentException("Comment must not be null.");
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
