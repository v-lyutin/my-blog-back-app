package com.amit.post.dto.response;

import java.util.List;

public record PostSearchResponse(
        List<PostPreviewDto> posts,
        boolean hasPrev,
        boolean hasNext,
        int lastPage) {
}
