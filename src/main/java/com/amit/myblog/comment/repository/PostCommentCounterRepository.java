package com.amit.myblog.comment.repository;


public interface PostCommentCounterRepository {

    void incrementCommentsCountByPostId(long postId);

    void decrementCommentsCountByPostId(long postId);

}
