package com.amit.myblog.tag.repository.jdbc;

import com.amit.myblog.tag.model.Tag;
import com.amit.myblog.tag.repository.TagRepository;
import com.amit.myblog.tag.repository.jdbc.mapper.PostTagRowMapper;
import com.amit.myblog.tag.repository.jdbc.mapper.TagRowMapper;
import com.amit.myblog.tag.repository.jdbc.sql.TagQueryHolder;
import com.amit.myblog.tag.repository.jdbc.util.PostTag;
import com.amit.myblog.common.util.TagNameExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Repository
public final class JdbcTagRepository implements TagRepository {

    private static final RowMapper<Tag> TAG_MAPPER = TagRowMapper.rowMapper();

    private static final RowMapper<PostTag> POST_TAG_MAPPER = PostTagRowMapper.rowMapper();

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public JdbcTagRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public Set<Tag> findTagsByPostId(long postId) {
        List<Tag> tags = this.namedParameterJdbcTemplate.query(
                TagQueryHolder.FIND_BY_POST_ID,
                new MapSqlParameterSource("postId", postId),
                TAG_MAPPER
        );
        return new HashSet<>(tags);
    }

    @Override
    public Set<Tag> ensureTagsExist(Collection<String> tagNames) {
        if (CollectionUtils.isEmpty(tagNames)) {
            return Collections.emptySet();
        }

        List<Tag> savedMissingTags = this.saveMissingTags(tagNames);
        List<String> savedMissingTagNames = TagNameExtractor.extractTagNames(savedMissingTags).stream().toList();

        List<String> alreadyExistingTagNames = this.filterAlreadyExistingTagNames(tagNames, savedMissingTagNames);
        List<Tag> alreadyExistingTags = alreadyExistingTagNames.isEmpty() ? List.of() : this.findTagsByNames(alreadyExistingTagNames);

        Set<Tag> result = new HashSet<>(savedMissingTagNames.size() + alreadyExistingTagNames.size());
        result.addAll(savedMissingTags);
        result.addAll(alreadyExistingTags);
        return result;
    }

    @Override
    public void attachTagsToPost(long postId, Collection<Long> tagIds) {
        if (CollectionUtils.isEmpty(tagIds)) {
            return;
        }
        SqlParameterSource[] batch = tagIds.stream()
                .filter(Objects::nonNull)
                .distinct()
                .map(tagId -> new MapSqlParameterSource()
                        .addValue("postId", postId)
                        .addValue("tagId", tagId)
                )
                .toArray(SqlParameterSource[]::new);
        if (batch.length > 0) {
            this.namedParameterJdbcTemplate.batchUpdate(TagQueryHolder.ATTACH_TO_POST, batch);
        }
    }

    @Override
    public void replacePostTags(long postId, Collection<Long> tagIds) {
        this.namedParameterJdbcTemplate.update(
                TagQueryHolder.DELETE_BY_POST_ID,
                new MapSqlParameterSource("postId", postId)
        );
        if (!CollectionUtils.isEmpty(tagIds)) {
            this.attachTagsToPost(postId, tagIds);
        }
    }

    @Override
    public Map<Long, Set<Tag>> findTagsByPostIds(Collection<Long> postIds) {
        if (CollectionUtils.isEmpty(postIds)) {
            return Collections.emptyMap();
        }

        List<PostTag> postTags = this.namedParameterJdbcTemplate.query(
                TagQueryHolder.FIND_BY_POST_IDS,
                new MapSqlParameterSource("postIds", postIds),
                POST_TAG_MAPPER
        );

        Map<Long, Set<Tag>> postTagsStorage = new HashMap<>(postIds.size());
        for (Long id : postIds) {
            postTagsStorage.put(id, new HashSet<>());
        }
        for (PostTag postTag : postTags) {
            postTagsStorage.get(postTag.postId()).add(postTag.tag());
        }
        return postTagsStorage;
    }

    private List<Tag> saveMissingTags(Collection<String> tagNames) {
        MapSqlParameterSource params = new MapSqlParameterSource("names", tagNames.toArray(new String[0]));
        return this.namedParameterJdbcTemplate.query(
                TagQueryHolder.SAVE_MISSING_TAGS,
                params,
                TAG_MAPPER
        );
    }

    private List<Tag> findTagsByNames(Collection<String> tagNames) {
        MapSqlParameterSource params = new MapSqlParameterSource("names", tagNames);
        return this.namedParameterJdbcTemplate.query(
                TagQueryHolder.FIND_BY_NAMES,
                params,
                TAG_MAPPER
        );
    }

    private List<String> filterAlreadyExistingTagNames(Collection<String> originalTagNames, Collection<String> existingTagNames) {
        return originalTagNames.stream()
                .filter(name -> !existingTagNames.contains(name))
                .toList();
    }

}
