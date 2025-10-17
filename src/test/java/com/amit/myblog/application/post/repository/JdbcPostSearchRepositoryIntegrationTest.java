package com.amit.myblog.application.post.repository;

import com.amit.myblog.common.BaseDaoIntegrationTest;
import com.amit.myblog.common.util.PostDaoTestFixtures;
import com.amit.myblog.common.util.PostTagDaoTestFixtures;
import com.amit.myblog.common.util.TagDaoTestFixtures;
import com.amit.myblog.post.model.Post;
import com.amit.myblog.post.repository.PostSearchRepository;
import com.amit.myblog.post.repository.jdbc.JdbcPostSearchRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@ActiveProfiles(value = "test")
@Import(value = JdbcPostSearchRepository.class)
class JdbcPostSearchRepositoryIntegrationTest extends BaseDaoIntegrationTest {

    @Autowired
    private PostSearchRepository postSearchRepository;

    @Test
    @DisplayName(value = "Should find posts by title substring only")
    void search_shouldFindByTitleSubstringOnly() {
        long post1Id = PostDaoTestFixtures.insertPostAndReturnId(this.jdbcTemplate, "Java Basics", "Text");
        long post2Id = PostDaoTestFixtures.insertPostAndReturnId(this.jdbcTemplate, "Advanced Java", "Text");
        long post3Id = PostDaoTestFixtures.insertPostAndReturnId(this.jdbcTemplate, "Kotlin Guide", "Text");

        List<Post> out = this.postSearchRepository.search("Java", Set.of(), 10, 0);
        Set<Long> postIds = getPostIds(out);

        assertThat(postIds).contains(post1Id, post2Id).doesNotContain(post3Id);
    }

    @Test
    @DisplayName(value = "Should find posts that contain all requested tags")
    void search_shouldFindPostsThatContainAllRequestedTags() {
        long post1Id = PostDaoTestFixtures.insertPostAndReturnId(this.jdbcTemplate, "post 1", "Text");
        long post2Id = PostDaoTestFixtures.insertPostAndReturnId(this.jdbcTemplate, "post 2", "Text");
        long post3Id = PostDaoTestFixtures.insertPostAndReturnId(this.jdbcTemplate, "post 3", "Text");

        long tag1Id = TagDaoTestFixtures.insertTagAndReturnId(this.namedParameterJdbcTemplate, "tag1");
        long tag2Id = TagDaoTestFixtures.insertTagAndReturnId(this.namedParameterJdbcTemplate, "tag2");
        long tag3Id = TagDaoTestFixtures.insertTagAndReturnId(this.namedParameterJdbcTemplate, "tag3");
        long tag4Id = TagDaoTestFixtures.insertTagAndReturnId(this.namedParameterJdbcTemplate, "tag4");

        PostTagDaoTestFixtures.linkTagToPost(this.namedParameterJdbcTemplate, post1Id, tag1Id);
        PostTagDaoTestFixtures.linkTagToPost(this.namedParameterJdbcTemplate, post1Id, tag2Id);

        PostTagDaoTestFixtures.linkTagToPost(this.namedParameterJdbcTemplate, post2Id, tag1Id);
        PostTagDaoTestFixtures.linkTagToPost(this.namedParameterJdbcTemplate, post2Id, tag4Id);

        PostTagDaoTestFixtures.linkTagToPost(this.namedParameterJdbcTemplate, post3Id, tag2Id);
        PostTagDaoTestFixtures.linkTagToPost(this.namedParameterJdbcTemplate, post3Id, tag3Id);

        List<Post> out1 = this.postSearchRepository.search(null, Set.of("tag1", "tag2"), 10, 0);
        assertThat(getPostIds(out1)).containsExactly(post1Id);

        List<Post> out2 = this.postSearchRepository.search(null, Set.of("tag2", "tag3"), 10, 0);
        assertThat(getPostIds(out2)).containsExactly(post3Id);

        List<Post> out3 = this.postSearchRepository.search(null, Set.of("tag4"), 10, 0);
        assertThat(getPostIds(out3)).containsExactly(post2Id);

        List<Post> out4 = this.postSearchRepository.search(null, Set.of("tag1", "tag3", "tag4"), 10, 0);
        assertThat(getPostIds(out4)).isEmpty();
    }

    @Test
    @DisplayName(value = "Should combine title and tags filters")
    void search_shouldCombineTitleAndTagsFilters() {
        long post1Id = PostDaoTestFixtures.insertPostAndReturnId(this.jdbcTemplate, "Spring Java Tips", "Text");
        long post2Id = PostDaoTestFixtures.insertPostAndReturnId(this.jdbcTemplate, "Java Tips", "Text");
        long post3Id = PostDaoTestFixtures.insertPostAndReturnId(this.jdbcTemplate, "Spring Boot Guide", "Text");

        long tag1Id = TagDaoTestFixtures.insertTagAndReturnId(this.namedParameterJdbcTemplate, "tips");
        long tag2Id = TagDaoTestFixtures.insertTagAndReturnId(this.namedParameterJdbcTemplate, "java");

        PostTagDaoTestFixtures.linkTagToPost(this.namedParameterJdbcTemplate, post1Id, tag1Id);
        PostTagDaoTestFixtures.linkTagToPost(this.namedParameterJdbcTemplate, post1Id, tag2Id);
        PostTagDaoTestFixtures.linkTagToPost(this.namedParameterJdbcTemplate, post2Id, tag1Id);
        PostTagDaoTestFixtures.linkTagToPost(this.namedParameterJdbcTemplate, post3Id, tag2Id);

        List<Post> out = this.postSearchRepository.search("Java", Set.of("tips", "java"), 10, 0);

        assertThat(getPostIds(out)).containsExactly(post1Id);
    }

    @Test
    @DisplayName(value = "Should honor limit and offset with stable DESC id order")
    void search_shouldHonorLimitAndOffsetWithStableDescIdOrder() {
        long post1Id = PostDaoTestFixtures.insertPostAndReturnId(this.jdbcTemplate, "Java 1", "Text");
        long post2Id = PostDaoTestFixtures.insertPostAndReturnId(this.jdbcTemplate, "Java 2", "Text");
        long post3Id = PostDaoTestFixtures.insertPostAndReturnId(this.jdbcTemplate, "Java 3", "Text");

        List<Post> page1 = this.postSearchRepository.search("Java", Set.of(), 1, 0);
        List<Post> page2 = this.postSearchRepository.search("Java", Set.of(), 1, 1);

        assertThat(page1).hasSize(1);
        assertThat(page2).hasSize(1);
        assertThat(page1.getFirst().getId()).isNotEqualTo(page2.getFirst().getId());
        assertThat(page1.getFirst().getId()).isGreaterThan(page2.getFirst().getId());

        List<Post> pageBig = this.postSearchRepository.search("Java", Set.of(), 2, 0);
        assertThat(pageBig).hasSizeBetween(1, 2);
        assertThat(getPostIds(pageBig)).isSubsetOf(Set.of(post1Id, post2Id, post3Id));
    }

    @Test
    @DisplayName(value = "Should return accurate total count for the same filters")
    void count_shouldReturnAccurateTotalForSameFilters() {
        long post1Id = PostDaoTestFixtures.insertPostAndReturnId(this.jdbcTemplate, "Hello Java", "Text");
        long post2Id = PostDaoTestFixtures.insertPostAndReturnId(this.jdbcTemplate, "Java World", "Text");
        PostDaoTestFixtures.insertPostAndReturnId(this.jdbcTemplate, "Kotlin Only", "body");

        long tag1Id = TagDaoTestFixtures.insertTagAndReturnId(this.namedParameterJdbcTemplate, "tag1");
        long tag2Id = TagDaoTestFixtures.insertTagAndReturnId(this.namedParameterJdbcTemplate, "tag2");

        PostTagDaoTestFixtures.linkTagToPost(this.namedParameterJdbcTemplate, post1Id, tag1Id);
        PostTagDaoTestFixtures.linkTagToPost(this.namedParameterJdbcTemplate, post1Id, tag2Id);
        PostTagDaoTestFixtures.linkTagToPost(this.namedParameterJdbcTemplate, post2Id, tag1Id);

        long total = this.postSearchRepository.count("Java", Set.of("tag1"));
        assertThat(total).isEqualTo(2L);

        long totalStrict = this.postSearchRepository.count("Java", Set.of("tag1", "tag2"));
        assertThat(totalStrict).isEqualTo(1L);
    }

    private static Set<Long> getPostIds(List<Post> posts) {
        return posts.stream().map(Post::getId).collect(Collectors.toSet());
    }

}