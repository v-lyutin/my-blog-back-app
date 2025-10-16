package com.amit.myblog.common;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public abstract class BaseDaoIntegrationTest {

    private static final PostgreSQLContainer<?> POSTGRES_CONTAINER = new PostgreSQLContainer<>(DockerImageName.parse("postgres:17.6-alpine3.22"));

    static {
        POSTGRES_CONTAINER.start();
    }

    @DynamicPropertySource
    private static void configureDatasource(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.datasource.url", POSTGRES_CONTAINER::getJdbcUrl);
        dynamicPropertyRegistry.add("spring.datasource.username", POSTGRES_CONTAINER::getUsername);
        dynamicPropertyRegistry.add("spring.datasource.password", POSTGRES_CONTAINER::getPassword);
    }

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @Autowired
    protected NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @BeforeEach
    void cleanDatabase() {
        this.jdbcTemplate.update("""
                    TRUNCATE TABLE my_blog.post_images,
                                   my_blog.comments,
                                   my_blog.post_tag,
                                   my_blog.tags,
                                   my_blog.posts
                    RESTART IDENTITY CASCADE
                """);
    }

}
