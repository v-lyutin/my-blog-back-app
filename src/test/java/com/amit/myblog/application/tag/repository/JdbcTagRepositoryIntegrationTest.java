package com.amit.myblog.application.tag.repository;

import com.amit.myblog.common.BaseDaoIntegrationTest;
import com.amit.myblog.common.util.PostDaoTestFixtures;
import com.amit.myblog.common.util.PostTagDaoTestFixtures;
import com.amit.myblog.common.util.TagDaoTestFixtures;
import com.amit.myblog.tag.model.Tag;
import com.amit.myblog.tag.repository.TagRepository;
import com.amit.myblog.tag.repository.jdbc.JdbcTagRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@ActiveProfiles(value = "test")
@Import(value = JdbcTagRepository.class)
class JdbcTagRepositoryIntegrationTest extends BaseDaoIntegrationTest {

    @Autowired
    private TagRepository tagRepository;

    @Test
    @DisplayName(value = "Should return empty set when post has no tags")
    void findTagsByPostId_shouldReturnEmptySetWhenPostHasNoTags() {
        long postId = PostDaoTestFixtures.insertPostAndReturnId(this.jdbcTemplate, "Title", "Text");

        Set<Tag> out = this.tagRepository.findTagsByPostId(postId);

        assertThat(out).isNotNull().isEmpty();
    }

    @Test
    @DisplayName(value = "Should return all tags attached to post")
    void findTagsByPostId_shouldReturnAllTagsAttachedToPost() {
        long postId = PostDaoTestFixtures.insertPostAndReturnId(this.jdbcTemplate, "Title", "Text");
        long tag1Id = TagDaoTestFixtures.insertTagAndReturnId(this.namedParameterJdbcTemplate, "tag1");
        long tag2Id = TagDaoTestFixtures.insertTagAndReturnId(this.namedParameterJdbcTemplate, "tag2");
        PostTagDaoTestFixtures.linkTagToPost(this.namedParameterJdbcTemplate, postId, tag1Id);
        PostTagDaoTestFixtures.linkTagToPost(this.namedParameterJdbcTemplate, postId, tag2Id);

        Set<String> tagNames = getTagNames(this.tagRepository.findTagsByPostId(postId));

        assertThat(tagNames).isEqualTo(Set.of("tag1", "tag2"));
    }

    @Test
    @DisplayName(value = "Should return empty set for ensureTagsExist when input is empty")
    void ensureTagsExist_shouldReturnEmptySetWhenInputIsEmpty() {
        Set<Tag> tags = this.tagRepository.ensureTagsExist(Collections.emptySet());

        assertThat(tags).isNotNull().isEmpty();
    }

    @Test
    @DisplayName(value = "Should create missing tags and return union of existing and new")
    void ensureTagsExist_shouldCreateMissingTagsAndReturnUnionOfExistingAndNew() {
        long existingTag1Id = TagDaoTestFixtures.insertTagAndReturnId(this.namedParameterJdbcTemplate, "tag1");
        assertThat(existingTag1Id).isPositive();

        Set<Tag> out = this.tagRepository.ensureTagsExist(Set.of("tag1", "tag2", "tag3"));
        assertThat(getTagNames(out)).isEqualTo(Set.of("tag1", "tag2", "tag3"));

        Set<Tag> out2 = this.tagRepository.ensureTagsExist(Set.of("tag1", "tag2", "tag3"));
        assertThat(getTagNames(out2)).isEqualTo(Set.of("tag1", "tag2", "tag3"));
    }

    @Test
    @DisplayName(value = "Should attach tags to post and ignore nulls/duplicates")
    void attachTagsToPost_shouldAttachAndIgnoreNullsAndDuplicates() {
        long postId = PostDaoTestFixtures.insertPostAndReturnId(this.jdbcTemplate, "Title", "Text");
        long tag1Id = TagDaoTestFixtures.insertTagAndReturnId(this.namedParameterJdbcTemplate, "tag1");
        long tag2Id = TagDaoTestFixtures.insertTagAndReturnId(this.namedParameterJdbcTemplate, "tag2");

        this.tagRepository.attachTagsToPost(postId, Arrays.asList(tag1Id, null, tag1Id, tag2Id));

        assertThat(getTagNames(this.tagRepository.findTagsByPostId(postId)))
                .isEqualTo(Set.of("tag1", "tag2"));
    }

    @Test
    @DisplayName(value = "Should no-op on attachTagsToPost when input is empty")
    void attachTagsToPost_shouldNoopWhenInputIsEmpty() {
        long postId = PostDaoTestFixtures.insertPostAndReturnId(this.jdbcTemplate, "Title", "Text");

        this.tagRepository.attachTagsToPost(postId, Collections.emptyList());

        assertThat(this.tagRepository.findTagsByPostId(postId)).isEmpty();
    }

    @Test
    @DisplayName(value = "Should replace existing tags with provided set")
    void replacePostTags_shouldReplaceExistingTagsWithProvidedSet() {
        long postId = PostDaoTestFixtures.insertPostAndReturnId(this.jdbcTemplate, "Title", "Text");
        long oldTagId = TagDaoTestFixtures.insertTagAndReturnId(this.namedParameterJdbcTemplate, "old");
        long newTagId = TagDaoTestFixtures.insertTagAndReturnId(this.namedParameterJdbcTemplate, "new");
        PostTagDaoTestFixtures.linkTagToPost(this.namedParameterJdbcTemplate, postId, oldTagId);

        this.tagRepository.replacePostTags(postId, List.of(newTagId));

        assertThat(getTagNames(this.tagRepository.findTagsByPostId(postId)))
                .isEqualTo(Set.of("new"));
    }

    @Test
    @DisplayName(value = "Should clear tags when replacePostTags called with empty collection")
    void replacePostTags_shouldClearTagsWhenEmptyCollectionProvided() {
        long postId = PostDaoTestFixtures.insertPostAndReturnId(this.jdbcTemplate, "Title", "Text");
        long tag1Id = TagDaoTestFixtures.insertTagAndReturnId(this.namedParameterJdbcTemplate, "tag1");
        long tag2Id = TagDaoTestFixtures.insertTagAndReturnId(this.namedParameterJdbcTemplate, "tag2");
        PostTagDaoTestFixtures.linkTagToPost(this.namedParameterJdbcTemplate, postId, tag1Id);
        PostTagDaoTestFixtures.linkTagToPost(this.namedParameterJdbcTemplate, postId, tag2Id);

        this.tagRepository.replacePostTags(postId, Collections.emptyList());

        assertThat(this.tagRepository.findTagsByPostId(postId)).isEmpty();
    }

    @Test
    @DisplayName(value = "Should return map with entries for all requested posts, including empty sets")
    void findTagsByPostIds_shouldReturnMapWithEntriesForAllRequestedPostsIncludingEmptySets() {
        long post1Id = PostDaoTestFixtures.insertPostAndReturnId(this.jdbcTemplate, "Title 1", "Text 1");
        long post2Id = PostDaoTestFixtures.insertPostAndReturnId(this.jdbcTemplate, "Title 2", "Text 2");
        long post3Id = PostDaoTestFixtures.insertPostAndReturnId(this.jdbcTemplate, "Title 3", "Text 3");
        long tag1Id = TagDaoTestFixtures.insertTagAndReturnId(this.namedParameterJdbcTemplate, "tag1");
        long tag2Id = TagDaoTestFixtures.insertTagAndReturnId(this.namedParameterJdbcTemplate, "tag2");

        PostTagDaoTestFixtures.linkTagToPost(this.namedParameterJdbcTemplate, post1Id, tag1Id);
        PostTagDaoTestFixtures.linkTagToPost(this.namedParameterJdbcTemplate, post1Id, tag2Id);
        PostTagDaoTestFixtures.linkTagToPost(this.namedParameterJdbcTemplate, post2Id, tag2Id);

        Map<Long, Set<Tag>> tags = this.tagRepository.findTagsByPostIds(List.of(post1Id, post2Id, post3Id));

        assertThat(tags).hasSize(3);
        assertThat(getTagNames(tags.get(post1Id))).isEqualTo(Set.of("tag1", "tag2"));
        assertThat(getTagNames(tags.get(post2Id))).isEqualTo(Set.of("tag2"));
        assertThat(tags.get(post3Id)).isEmpty();
    }

    @Test
    @DisplayName(value = "Should return empty map when input list is null or empty")
    void findTagsByPostIds_shouldReturnEmptyMapWhenInputListIsNullOrEmpty() {
        assertThat(this.tagRepository.findTagsByPostIds(null)).isEmpty();
        assertThat(this.tagRepository.findTagsByPostIds(Collections.emptyList())).isEmpty();
    }

    private static Set<String> getTagNames(Collection<Tag> tags) {
        return tags.stream().map(Tag::getName).collect(Collectors.toSet());
    }

}