package com.amit.post.service.image;

import com.amit.post.repository.PostImageRepository;
import com.amit.post.service.image.exception.ImageNotFoundException;
import com.amit.post.service.image.exception.ImageUpsertException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public final class DefaultPostImageService implements PostImageService {

    private final PostImageRepository postImageRepository;

    public DefaultPostImageService(PostImageRepository postImageRepository) {
        this.postImageRepository = postImageRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] getByPostId(long postId) {
        return this.postImageRepository.findByPostId(postId)
                .orElseThrow(() -> new ImageNotFoundException("Image for post %d not found".formatted(postId)));
    }

    @Override
    @Transactional
    public void upsertByPostId(long postId, byte[] data) {
        boolean isSaved = this.postImageRepository.upsertByPostId(postId, data);
        if (!isSaved) {
            throw new ImageUpsertException("Failed to upsert image for post %d".formatted(postId));
        }
    }

}
