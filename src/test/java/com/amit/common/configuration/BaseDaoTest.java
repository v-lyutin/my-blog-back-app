package com.amit.common.configuration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = DaoTestConfiguration.class)
public abstract class BaseDaoTest {

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
