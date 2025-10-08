package com.amit.post.service;

public interface PostImageService {

    byte[] getByPostId(long postId);

    void upsertByPostId(long postId, byte[] data);

}
