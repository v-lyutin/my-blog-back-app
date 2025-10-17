package com.amit.myblog.post.repository;

import java.util.Optional;

public interface PostImageRepository {

    Optional<byte[]> findByPostId(long postId);

    boolean upsertByPostId(long postId, byte[] data);

}
