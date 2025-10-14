package com.amit.application.post.repository;

import com.amit.common.configuration.BaseDaoTest;
import com.amit.common.util.DaoTestHelper;
import com.amit.post.model.Post;
import com.amit.post.repository.PostSearchRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class JdbcSearchRepositoryIntegrationTest extends BaseDaoTest {

    @Autowired
    private DaoTestHelper daoTestHelper;

    @Autowired
    private PostSearchRepository postSearchRepository;

    @Test
    @DisplayName(value = "Should find posts by title substring only")
    void search_shouldFindByTitleOnly() {
        long post1 = this.daoTestHelper.insertPost("Java Basics", "body");
        long post2 = this.daoTestHelper.insertPost("Advanced Java", "body");
        long post3 = this.daoTestHelper.insertPost("Kotlin Guide", "body");

        List<Post> out = this.postSearchRepository.search("Java", Set.of(), 10, 0);
        Set<Long> postIds = getPostIds(out);

        assertTrue(postIds.contains(post1));
        assertTrue(postIds.contains(post2));
        assertFalse(postIds.contains(post3));
    }

    @Test
    @DisplayName(value = "Should find posts that have all requested tags only")
    void search_shouldFindByTagsOnly_allRequested() {
        long post1 = this.daoTestHelper.insertPost("post 1", "body");
        long post2 = this.daoTestHelper.insertPost("post 2", "body");
        long post3 = this.daoTestHelper.insertPost("post 3", "body");

        long tag1 = this.daoTestHelper.insertTag("tag1");
        long tag2 = this.daoTestHelper.insertTag("tag2");
        long tag3 = this.daoTestHelper.insertTag("tag3");
        long tag4 = this.daoTestHelper.insertTag("tag4");

        // post1 -> tag1, tag2
        this.daoTestHelper.linkTag(post1, tag1);
        this.daoTestHelper.linkTag(post1, tag2);
        // post2 -> tag1, tag4
        this.daoTestHelper.linkTag(post2, tag1);
        this.daoTestHelper.linkTag(post2, tag4);
        // post3 -> tag2, tag3
        this.daoTestHelper.linkTag(post3, tag2);
        this.daoTestHelper.linkTag(post3, tag3);

        // "tag1 AND tag2" -> only post1
        List<Post> out1 = this.postSearchRepository.search(null, Set.of("tag1", "tag2"), 10, 0);
        assertEquals(Set.of(post1), getPostIds(out1));

        // "tag2 AND tag3" -> only post3
        List<Post> out2 = this.postSearchRepository.search(null, Set.of("tag2", "tag3"), 10, 0);
        assertEquals(Set.of(post3), getPostIds(out2));

        // single rare tag: "tag4" -> only post2
        List<Post> out3 = this.postSearchRepository.search(null, Set.of("tag4"), 10, 0);
        assertEquals(Set.of(post2), getPostIds(out3));

        // intersection that nobody has: "tag1 AND tag3 AND tag4" -> empty
        List<Post> out4 = this.postSearchRepository.search(null, Set.of("tag1", "tag3", "tag4"), 10, 0);
        assertTrue(getPostIds(out4).isEmpty());
    }

    @Test
    @DisplayName(value = "Should combine title and tags filters")
    void search_shouldCombineTitleAndTags() {
        long post1 = this.daoTestHelper.insertPost("Spring Java Tips", "body");
        long post2 = this.daoTestHelper.insertPost("Java Tips", "body");
        long post3 = this.daoTestHelper.insertPost("Spring Boot Guide", "body");

        long tag1 = this.daoTestHelper.insertTag("tips");
        long tag2 = this.daoTestHelper.insertTag("java");

        // post1 -> tips, java
        this.daoTestHelper.linkTag(post1, tag1);
        this.daoTestHelper.linkTag(post1, tag2);
        // post2 -> tips only
        this.daoTestHelper.linkTag(post2, tag1);
        // post3 -> java only
        this.daoTestHelper.linkTag(post3, tag2);

        // title contains "Java", AND tags contain both "tips" and "java" -> only p1
        List<Post> out = this.postSearchRepository.search("Java", Set.of("tips", "java"), 10, 0);
        Set<Long> postIds = getPostIds(out);

        assertEquals(Set.of(post1), postIds);
    }

    @Test
    @DisplayName(value = "Should honor limit and offset (basic pagination sanity)")
    void search_shouldHonorLimitAndOffset() {
        long post1 = this.daoTestHelper.insertPost("Java 1", "body");
        long post2 = this.daoTestHelper.insertPost("Java 2", "body");
        long post3 = this.daoTestHelper.insertPost("Java 3", "body");

        // first page
        List<Post> page1 = this.postSearchRepository.search("Java", Set.of(), 1, 0);
        assertEquals(1, page1.size());

        // second page (different offset)
        List<Post> page2 = this.postSearchRepository.search("Java", Set.of(), 1, 1);
        assertEquals(1, page2.size());

        // they should not be the same row for a stable DB order (by id)
        assertNotEquals(page1.getFirst().getId(), page2.getFirst().getId());

        // size should not exceed limit
        List<Post> pageBig = this.postSearchRepository.search("Java", Set.of(), 2, 0);
        assertTrue(pageBig.size() <= 2);
        // all returned are one of inserted
        assertTrue(getPostIds(pageBig).containsAll(Set.of(post1, post2)) || getPostIds(pageBig).containsAll(Set.of(post2, post3)) || getPostIds(pageBig).containsAll(Set.of(post1, post3)));
    }

    @Test
    @DisplayName(value = "Should return accurate total count for the same filters")
    void count_shouldReturnAccurateCount() {
        long post1 = this.daoTestHelper.insertPost("Hello Java", "body");
        long post2 = this.daoTestHelper.insertPost("Java World", "body");
        this.daoTestHelper.insertPost("Kotlin Only", "body");

        long tag1 = this.daoTestHelper.insertTag("tag1");
        long tag2 = this.daoTestHelper.insertTag("tag2");

        // post1 -> tag1, tag2
        this.daoTestHelper.linkTag(post1, tag1);
        this.daoTestHelper.linkTag(post1, tag2);
        // post1 -> tag1
        this.daoTestHelper.linkTag(post2, tag1);

        long total = this.postSearchRepository.count("Java", Set.of("tag1"));
        // matches: p1 (a,b) and p2 (a)
        assertEquals(2L, total);

        long totalStrict = this.postSearchRepository.count("Java", Set.of("tag1", "tag2"));
        // matches: only p1 (has both a and b)
        assertEquals(1L, totalStrict);
    }

    private static Set<Long> getPostIds(List<Post> posts) {
        return posts.stream().map(Post::getId).collect(Collectors.toSet());
    }

}