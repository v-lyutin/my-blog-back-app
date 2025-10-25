package com.amit.myblog.post.service.impl;

import com.amit.myblog.common.util.Page;
import com.amit.myblog.post.model.Post;
import com.amit.myblog.post.model.PostView;
import com.amit.myblog.post.repository.PostSearchRepository;
import com.amit.myblog.post.service.PostSearchService;
import com.amit.myblog.post.service.util.RawQueryParser;
import com.amit.myblog.post.service.util.SearchCriteria;
import com.amit.myblog.tag.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class DefaultPostSearchService implements PostSearchService {

    private final PostSearchRepository postSearchRepository;

    private final TagService tagService;

    @Autowired
    public DefaultPostSearchService(PostSearchRepository postSearchRepository, TagService tagService) {
        this.postSearchRepository = postSearchRepository;
        this.tagService = tagService;
    }

    @Override
    public Page<PostView> searchPosts(String rawQuery, int pageNumber, int pageSize) {
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
        if (CollectionUtils.isEmpty(posts)) {
            return Collections.emptyList();
        }
        List<Long> postIds = posts.stream().map(Post::getId).toList();
        Map<Long, Set<String>> tagsByPostId = postIds.isEmpty()
                ? Collections.emptyMap()
                : this.tagService.getTagsByPostIds(postIds);
        return posts.stream()
                .map(post -> {
                    Set<String> tags = tagsByPostId.get(post.getId());
                    return new PostView(post, tags);
                })
                .toList();
    }

}
