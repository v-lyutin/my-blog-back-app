package com.amit.post.service.image;

public interface PostImageService {

    byte[] getByPostId(long postId);

    void upsertByPostId(long postId, byte[] data);

}
