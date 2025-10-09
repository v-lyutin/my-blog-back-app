package com.amit.post.service;

import java.util.Optional;

public interface PostImageService {

    Optional<byte[]> getByPostId(long postId);

    void upsertByPostId(long postId, byte[] data);

}
