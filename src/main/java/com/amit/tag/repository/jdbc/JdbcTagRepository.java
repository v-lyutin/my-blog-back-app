package com.amit.tag.repository.jdbc;

import com.amit.tag.model.Tag;
import com.amit.tag.repository.TagRepository;
import com.amit.tag.repository.jdbc.sql.TagQueryHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public final class JdbcTagRepository implements TagRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final RowMapper<Tag> rowMapper;

    @Autowired
    public JdbcTagRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                             @Qualifier("tagRowMapper") RowMapper<Tag> rowMapper) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.rowMapper = rowMapper;
    }

    @Override
    public Set<Tag> findTagsByPostId(long postId) {
        List<Tag> tags = this.namedParameterJdbcTemplate.query(
                TagQueryHolder.FIND_TAGS_BY_POST_ID,
                new MapSqlParameterSource("postId", postId),
                this.rowMapper);
        return new HashSet<>(tags);
    }

    @Override
    public Set<Tag> ensureTagsExist(Collection<String> tagNames) {
        if (tagNames == null || tagNames.isEmpty()) {
            return Collections.emptySet();
        }

        List<Tag> savedMissingTags = this.saveMissingTags(tagNames);
        List<String> savedMissingTagNames = this.extractTagNames(savedMissingTags);

        List<String> alreadyExistingTagNames = this.filterAlreadyExistingTagNames(tagNames, savedMissingTagNames);
        List<Tag> alreadyExistingTags = alreadyExistingTagNames.isEmpty() ? List.of() : this.findTagsByNames(alreadyExistingTagNames);

        Set<Tag> result = new HashSet<>(savedMissingTagNames.size() + alreadyExistingTagNames.size());
        result.addAll(savedMissingTags);
        result.addAll(alreadyExistingTags);
        return result;
    }

    @Override
    public void attachTagsToPost(long postId, Collection<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
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
            this.namedParameterJdbcTemplate.batchUpdate(TagQueryHolder.ATTACH_TAGS_TO_POST, batch);
        }
    }

    @Override
    public void replacePostTags(long postId, Collection<Long> tagIds) {
        this.namedParameterJdbcTemplate.update(
                TagQueryHolder.DELETE_TAGS_BY_POST_ID,
                new MapSqlParameterSource("postId", postId)
        );
        if (tagIds != null && !tagIds.isEmpty()) {
            this.attachTagsToPost(postId, tagIds);
        }
    }

    private List<Tag> saveMissingTags(Collection<String> tagNames) {
        MapSqlParameterSource params = new MapSqlParameterSource("names", tagNames.toArray(new String[0]));
        return this.namedParameterJdbcTemplate.query(
                TagQueryHolder.SAVE_MISSING_TAGS,
                params,
                rowMapper
        );
    }

    private List<Tag> findTagsByNames(Collection<String> tagNames) {
        MapSqlParameterSource params = new MapSqlParameterSource("names", tagNames);
        return this.namedParameterJdbcTemplate.query(
                TagQueryHolder.FIND_TAGS_BY_NAMES,
                params,
                rowMapper
        );
    }

    private List<String> extractTagNames(Collection<Tag> tags) {
        return tags.stream()
                .map(Tag::getName)
                .toList();
    }

    private List<String> filterAlreadyExistingTagNames(Collection<String> originalTagNames, Collection<String> existingTagNames) {
        return originalTagNames.stream()
                .filter(name -> !existingTagNames.contains(name))
                .toList();
    }

}
