package com.amit.myblog.post.repository;

import java.util.OptionalLong;

public interface PostLikeRepository {

    OptionalLong incrementPostLikes(long postId);

}
