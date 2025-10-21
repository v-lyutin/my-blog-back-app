package com.amit.myblog.post.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface PostImageService {

    Optional<byte[]> getImageByPostId(long postId);

    void upsertImageByPostId(long postId, MultipartFile multipartFile);

}
