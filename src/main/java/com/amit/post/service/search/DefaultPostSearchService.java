package com.amit.post.service.search;

import com.amit.common.util.Page;
import com.amit.post.model.Post;
import com.amit.post.repository.PostSearchRepository;
import com.amit.post.service.search.model.SearchCriteria;
import com.amit.post.service.search.util.RawQueryParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public final class DefaultPostSearchService implements PostSearchService {

    private final PostSearchRepository postSearchRepository;

    @Autowired
    public DefaultPostSearchService(PostSearchRepository postSearchRepository) {
        this.postSearchRepository = postSearchRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Post> search(String rawQuery, int pageNumber, int pageSize) {
        int offset = (pageNumber - 1) * pageSize;
        SearchCriteria searchCriteria = RawQueryParser.parse(rawQuery);
        List<Post> posts = this.postSearchRepository.search(
                searchCriteria.titleQuery(),
                searchCriteria.tagNames(),
                pageSize,
                offset
        );
        long total = postSearchRepository.count(searchCriteria.titleQuery(), searchCriteria.tagNames());
        return new Page<>(posts, pageNumber, pageSize, total);
    }

}
