package com.amit.myblog.comment.service;

import com.amit.myblog.comment.model.event.CommentCreatedEvent;
import com.amit.myblog.comment.model.event.CommentDeletedEvent;
import com.amit.myblog.comment.repository.PostCommentCounterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public final class PostCommentCountCounterListener {

    private final PostCommentCounterRepository postCommentCounterRepository;

    @Autowired
    public PostCommentCountCounterListener(PostCommentCounterRepository postCommentCounterRepository) {
        this.postCommentCounterRepository = postCommentCounterRepository;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCommentCreatedEvent(CommentCreatedEvent commentCreatedEvent) {
        this.postCommentCounterRepository.incrementCommentsCountByPostId(commentCreatedEvent.postId());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCommentDeletedEvent(CommentDeletedEvent commentDeletedEvent) {
        this.postCommentCounterRepository.decrementCommentsCountByPostId(commentDeletedEvent.postId());
    }

}
