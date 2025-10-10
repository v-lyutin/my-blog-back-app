package com.amit.post.service.impl;

import com.amit.post.repository.PostCrudRepository;
import com.amit.post.repository.PostImageRepository;
import com.amit.post.service.PostImageService;
import com.amit.post.service.exception.ImageUpsertException;
import com.amit.post.service.util.ImageValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public final class DefaultPostImageService implements PostImageService {

    private final PostImageRepository postImageRepository;

    private final PostCrudRepository postCrudRepository;

    public DefaultPostImageService(PostImageRepository postImageRepository, PostCrudRepository postCrudRepository) {
        this.postImageRepository = postImageRepository;
        this.postCrudRepository = postCrudRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<byte[]> getByPostId(long postId) {
        return this.postImageRepository.findByPostId(postId);
    }

    @Override
    @Transactional
    public void upsertByPostId(long postId, byte[] data) {
        ImageValidator.validateSize(data);

        if (!this.postCrudRepository.existsById(postId)) {
            throw new ImageUpsertException("Cannot upsert image: post %d does not exist.".formatted(postId));
        }

        boolean isSaved = this.postImageRepository.upsertByPostId(postId, data);
        if (!isSaved) {
            throw new ImageUpsertException("Failed to upsert image for post %d.".formatted(postId));
        }
    }

}
