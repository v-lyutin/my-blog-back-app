package com.amit.post.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface PostImageService {

    Optional<byte[]> getByPostId(long postId);

    void upsertByPostId(long postId, MultipartFile multipartFile);

}
