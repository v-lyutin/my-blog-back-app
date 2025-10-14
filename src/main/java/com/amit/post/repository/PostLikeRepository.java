package com.amit.post.repository;

import java.util.OptionalLong;

public interface PostLikeRepository {

    OptionalLong incrementPostLikes(long postId);

}
