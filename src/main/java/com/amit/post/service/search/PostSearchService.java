package com.amit.post.service.search;

import com.amit.common.util.Page;
import com.amit.post.model.PostView;

public interface PostSearchService {

    Page<PostView> search(String rawQuery, int pageNumber, int pageSize);

}
