package com.amit.post.service.impl;

import com.amit.post.repository.PostImageRepository;
import com.amit.post.service.PostCrudService;
import com.amit.post.service.PostImageService;
import com.amit.post.service.exception.ImageUpsertException;
import com.amit.post.service.util.ImageValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
public final class DefaultPostImageService implements PostImageService {

    private final PostImageRepository postImageRepository;

    private final PostCrudService postCrudService;

    public DefaultPostImageService(PostImageRepository postImageRepository, PostCrudService postCrudService) {
        this.postImageRepository = postImageRepository;
        this.postCrudService = postCrudService;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<byte[]> getByPostId(long postId) {
        return this.postImageRepository.findByPostId(postId);
    }

    @Override
    @Transactional
    public void upsertByPostId(long postId, MultipartFile multipartFile) {
        try {
            this.postCrudService.ensurePostExists(postId);

            byte[] data = multipartFile.getBytes();
            ImageValidator.validateSize(data);

            boolean isSaved = this.postImageRepository.upsertByPostId(postId, data);
            if (!isSaved) {
                throw new ImageUpsertException("Failed to upsert image for post %d.".formatted(postId));
            }
        } catch (IOException exception) {
            throw new ImageUpsertException("Failed to read uploaded image.");
        }
    }

}
