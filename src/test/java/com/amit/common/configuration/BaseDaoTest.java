package com.amit.common.configuration;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = DaoTestConfiguration.class)
public abstract class BaseDaoTest {

    @BeforeAll
    static void ensureStarted() {
        DatabaseContainer.getInstance();
    }

    @DynamicPropertySource
    static void addDatabaseProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        PostgreSQLContainer<?> container = DatabaseContainer.getInstance();
        dynamicPropertyRegistry.add("test.jdbc.url", container::getJdbcUrl);
        dynamicPropertyRegistry.add("test.jdbc.username", container::getUsername);
        dynamicPropertyRegistry.add("test.jdbc.password", container::getPassword);
    }

    @Autowired
    protected DataSource dataSource;

    @BeforeEach
    void cleanDatabase() throws Exception {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("""
                    TRUNCATE TABLE my_blog.post_images,
                                       my_blog.comments,
                                       my_blog.post_tag,
                                       my_blog.tags,
                                       my_blog.posts
                    RESTART IDENTITY CASCADE
                    """);
        }
    }

}
