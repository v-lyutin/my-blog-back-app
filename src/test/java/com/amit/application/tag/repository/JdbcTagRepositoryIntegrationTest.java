package com.amit.application.tag.repository;

import com.amit.common.configuration.BaseDaoTest;
import com.amit.common.util.DaoTestHelper;
import com.amit.tag.model.Tag;
import com.amit.tag.repository.TagRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class JdbcTagRepositoryIntegrationTest extends BaseDaoTest {

    @Autowired
    DaoTestHelper daoTestHelper;

    @Autowired
    TagRepository tagRepository;

    @Test
    @DisplayName(value = "Should return empty set when post has no tags")
    void findTagsByPostId_noTags_returnsEmpty() {
        long postId = this.daoTestHelper.insertPost("Title", "Text");

        Set<Tag> out = this.tagRepository.findTagsByPostId(postId);

        assertNotNull(out);
        assertTrue(out.isEmpty());
    }

    @Test
    @DisplayName(value = "Should return all tags attached to post")
    void findTagsByPostId_hasTags_returnsAll() {
        long postId = this.daoTestHelper.insertPost("Title", "Text");
        long tag1 = this.daoTestHelper.insertTag("tag1");
        long tag2 = this.daoTestHelper.insertTag("tag2");
        this.daoTestHelper.linkTag(postId, tag1);
        this.daoTestHelper.linkTag(postId, tag2);

        Set<String> tagNames = getTagNames(this.tagRepository.findTagsByPostId(postId));

        assertEquals(Set.of("tag1", "tag2"), tagNames);
    }

    @Test
    @DisplayName(value = "Should return empty set for ensureTagsExist when input is empty")
    void ensureTagsExist_emptyTags_returnsEmpty() {
        Set<Tag> out = this.tagRepository.ensureTagsExist(Collections.emptySet());
        assertNotNull(out);
        assertTrue(out.isEmpty());
    }

    @Test
    @DisplayName(value = "Should create missing tags and return union of existing and new")
    void ensureTagsExist_mixedTags_createsMissingAndReturnsAll() {
        long existingTag1 = this.daoTestHelper.insertTag("tag1");
        assertTrue(existingTag1 > 0);

        Set<Tag> out = this.tagRepository.ensureTagsExist(Set.of("tag1", "tag2", "tag3"));

        assertEquals(Set.of("tag1", "tag2", "tag3"), getTagNames(out));

        Set<Tag> out2 = this.tagRepository.ensureTagsExist(Set.of("tag1", "tag2", "tag3"));
        assertEquals(Set.of("tag1", "tag2", "tag3"), getTagNames(out2));
    }

    @Test
    @DisplayName(value = "Should attach tags to post and ignore nulls/duplicates")
    void attachTagsToPost_attachesTags_ignoresNullsAndDuplicates() {
        long postId = this.daoTestHelper.insertPost("Title", "Text");
        long tag1 = this.daoTestHelper.insertTag("tag1");
        long tag2 = this.daoTestHelper.insertTag("tag2");

        this.tagRepository.attachTagsToPost(postId, Arrays.asList(tag1, null, tag1, tag2));

        assertEquals(Set.of("tag1", "tag2"), getTagNames(this.tagRepository.findTagsByPostId(postId)));
    }

    @Test
    @DisplayName(value = "Should no-op on attachTagsToPost when input is empty")
    void attachTagsToPost_emptyTags_noop() {
        long postId = this.daoTestHelper.insertPost("Title", "Text");

        this.tagRepository.attachTagsToPost(postId, Collections.emptyList());

        assertTrue(this.tagRepository.findTagsByPostId(postId).isEmpty());
    }

    @Test
    @DisplayName(value = "Should replace existing tags with provided set")
    void replacePostTags_replacesTags() {
        long postId = this.daoTestHelper.insertPost("Title", "Text");
        long oldTag = this.daoTestHelper.insertTag("old");
        long newTag = this.daoTestHelper.insertTag("new");
        this.daoTestHelper.linkTag(postId, oldTag);

        this.tagRepository.replacePostTags(postId, List.of(newTag));

        assertEquals(Set.of("new"), getTagNames(this.tagRepository.findTagsByPostId(postId)));
    }

    @Test
    @DisplayName(value = "Should clear tags when replacePostTags called with empty collection")
    void replacePostTags_emptyTags_clearsAll() {
        long postId = this.daoTestHelper.insertPost("Title", "Text");
        long tag1 = this.daoTestHelper.insertTag("tag1");
        long tag2 = this.daoTestHelper.insertTag("tag2");
        this.daoTestHelper.linkTag(postId, tag1);
        this.daoTestHelper.linkTag(postId, tag2);

        this.tagRepository.replacePostTags(postId, Collections.emptyList());

        assertTrue(this.tagRepository.findTagsByPostId(postId).isEmpty());
    }

    @Test
    @DisplayName(value = "Should return map with entries for all requested posts, including empty sets")
    void findTagsByPostIds_returnsMapWithEmptySets() {
        long post1 = this.daoTestHelper.insertPost("Title 1", "Text 1");
        long post2 = this.daoTestHelper.insertPost("Title 2", "Text 2");
        long post3 = this.daoTestHelper.insertPost("Title 3", "Text 3");
        long tag1 = this.daoTestHelper.insertTag("tag1");
        long tag2 = this.daoTestHelper.insertTag("tag2");

        this.daoTestHelper.linkTag(post1, tag1);
        this.daoTestHelper.linkTag(post1, tag2);
        this.daoTestHelper.linkTag(post2, tag2);

        Map<Long, Set<Tag>> out = this.tagRepository.findTagsByPostIds(List.of(post1, post2, post3));

        assertEquals(3, out.size());
        assertEquals(Set.of("tag1", "tag2"), getTagNames(out.get(post1)));
        assertEquals(Set.of("tag2"), getTagNames(out.get(post2)));
        assertTrue(out.get(post3).isEmpty());
    }

    @Test
    @DisplayName(value = "Should return empty map when input list is null or empty")
    void findTagsByPostIds_nullOrEmptyTags_returnsEmptyMap() {
        assertTrue(this.tagRepository.findTagsByPostIds(null).isEmpty());
        assertTrue(this.tagRepository.findTagsByPostIds(Collections.emptyList()).isEmpty());
    }
    

    private static Set<String> getTagNames(Collection<Tag> tags) {
        return tags.stream().map(Tag::getName).collect(Collectors.toSet());
    }

}