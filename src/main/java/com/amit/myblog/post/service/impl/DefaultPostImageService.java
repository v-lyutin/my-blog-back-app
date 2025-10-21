package com.amit.myblog.post.service.impl;

import com.amit.myblog.common.excpetion.ResourceNotFoundException;
import com.amit.myblog.post.repository.PostImageRepository;
import com.amit.myblog.post.repository.PostRepository;
import com.amit.myblog.post.service.PostImageService;
import com.amit.myblog.post.service.exception.ImageUpsertException;
import com.amit.myblog.post.service.exception.InvalidImageException;
import com.amit.myblog.post.service.util.ImageValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
public final class DefaultPostImageService implements PostImageService {

    private final PostImageRepository postImageRepository;

    private final PostRepository postRepository;

    public DefaultPostImageService(PostImageRepository postImageRepository, PostRepository postRepository) {
        this.postImageRepository = postImageRepository;
        this.postRepository = postRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<byte[]> getImageByPostId(long postId) {
        return this.postImageRepository.findByPostId(postId);
    }

    @Override
    @Transactional
    public void upsertImageByPostId(long postId, MultipartFile multipartFile) {
        try {
            if (multipartFile == null || multipartFile.isEmpty()) {
                throw new InvalidImageException("Uploaded file must not be empty.");
            }
            if (!this.postRepository.existsById(postId)) {
                throw new ResourceNotFoundException("Post with ID %d not found.".formatted(postId));
            }

            byte[] data = multipartFile.getBytes();
            ImageValidator.validateSize(data);

            boolean isUpserted = this.postImageRepository.upsertByPostId(postId, data);
            if (!isUpserted) {
                throw new ImageUpsertException("Failed to upsert image for post %d.".formatted(postId));
            }
        } catch (IOException exception) {
            throw new ImageUpsertException("Failed to read uploaded image.");
        }
    }

}
