package com.amit.post.service.search;

import com.amit.common.util.Page;
import com.amit.post.model.Post;

public interface PostSearchService {

    Page<Post> search(String rawQuery, int pageNumber, int pageSize);

}
