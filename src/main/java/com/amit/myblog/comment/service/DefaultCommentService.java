package com.amit.myblog.comment.service;

import com.amit.myblog.comment.model.Comment;
import com.amit.myblog.comment.repository.CommentRepository;
import com.amit.myblog.comment.repository.PostCommentCounterRepository;
import com.amit.myblog.common.excpetion.ResourceNotFoundException;
import com.amit.myblog.common.excpetion.ServiceException;
import com.amit.myblog.post.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DefaultCommentService implements CommentService {

    private final CommentRepository commentRepository;

    private final PostCommentCounterRepository postCommentCounterRepository;

    private final PostRepository postRepository;

    @Autowired
    public DefaultCommentService(CommentRepository commentRepository,
                                 PostCommentCounterRepository postCommentCounterRepository,
                                 PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.postCommentCounterRepository = postCommentCounterRepository;
        this.postRepository = postRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> getAllCommentsByPostId(long postId) {
        return this.commentRepository.findAllByPostId(postId);
    }

    @Override
    @Transactional(readOnly = true)
    public Comment getCommentByPostIdAndCommentId(long postId, long commentId) {
        return this.commentRepository.findByPostIdAndId(postId, commentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Comment with ID %d for post with ID %d not found.".formatted(commentId, postId)
                ));
    }

    @Override
    @Transactional
    public Comment addComment(long postId, Comment comment) {
        if (comment == null) {
            throw new ServiceException("Comment must not be null.");
        }
        if (postId != comment.getPostId()) {
            throw new ServiceException("IDs for post must not be different.");
        }
        if (!this.postRepository.existsById(postId)) {
            throw new ResourceNotFoundException("Post with ID %d not found.".formatted(postId));
        }
        Comment savedComment = this.commentRepository.save(comment);
        this.postCommentCounterRepository.incrementCommentsCountByPostId(postId);
        return savedComment;
    }

    @Override
    @Transactional
    public Comment editComment(long postId, long commentId, Comment comment) {
        if (comment == null) {
            throw new ServiceException("Comment must not be null.");
        }
        if (postId != comment.getPostId()) {
            throw new ServiceException("IDs for post must not be different.");
        }
        if (commentId != comment.getId()) {
            throw new ServiceException("IDs for comment must not be different");
        }
        return this.commentRepository.update(comment)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Comment with ID %d for post with ID %d not found.".formatted(comment.getId(), comment.getPostId())
                ));
    }

    @Override
    @Transactional
    public void deleteCommentByPostIdAndCommentId(long postId, long commentId) {
        boolean isDeleted = this.commentRepository.deleteByPostIdAndId(postId, commentId);
        if (!isDeleted) {
            throw new ResourceNotFoundException(
                    "Comment with ID %d for post with ID %d not found.".formatted(commentId, postId)
            );
        }
        this.postCommentCounterRepository.decrementCommentsCountByPostId(postId);
    }

}
