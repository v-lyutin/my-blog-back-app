package com.amit.myblog.post.service;

import com.amit.myblog.common.util.Page;
import com.amit.myblog.post.model.PostView;

public interface PostSearchService {

    Page<PostView> search(String rawQuery, int pageNumber, int pageSize);

}
