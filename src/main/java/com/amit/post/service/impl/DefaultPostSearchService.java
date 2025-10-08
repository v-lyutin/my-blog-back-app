package com.amit.post.service.impl;

import com.amit.common.util.Page;
import com.amit.post.model.Post;
import com.amit.post.model.PostView;
import com.amit.post.repository.PostSearchRepository;
import com.amit.post.service.PostSearchService;
import com.amit.post.service.util.SearchCriteria;
import com.amit.post.service.util.RawQueryParser;
import com.amit.tag.model.Tag;
import com.amit.tag.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public final class DefaultPostSearchService implements PostSearchService {

    private final PostSearchRepository postSearchRepository;

    private final TagService tagService;

    @Autowired
    public DefaultPostSearchService(PostSearchRepository postSearchRepository, TagService tagService) {
        this.postSearchRepository = postSearchRepository;
        this.tagService = tagService;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostView> search(String rawQuery, int pageNumber, int pageSize) {
        int offset = Math.max(0, (pageNumber - 1) * pageSize);
        SearchCriteria searchCriteria = RawQueryParser.parse(rawQuery);
        List<Post> posts = this.postSearchRepository.search(
                searchCriteria.titleQuery(),
                searchCriteria.tagNames(),
                pageSize,
                offset
        );
        List<PostView> postViews = this.attachTagsAndMapToPostViews(posts);
        long total = this.postSearchRepository.count(searchCriteria.titleQuery(), searchCriteria.tagNames());
        return new Page<>(postViews, pageNumber, pageSize, total);
    }

    private List<PostView> attachTagsAndMapToPostViews(List<Post> posts) {
        if (posts == null || posts.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> postIds = posts.stream().map(Post::getId).toList();
        Map<Long, Set<Tag>> tagsByPostId = postIds.isEmpty()
                ? Collections.emptyMap()
                : this.tagService.getTagsByPostIds(postIds);
        return posts.stream()
                .map(post -> new PostView(post, tagsByPostId.getOrDefault(post.getId(), Collections.emptySet())))
                .toList();
    }

}
