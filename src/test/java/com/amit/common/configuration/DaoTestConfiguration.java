package com.amit.common.configuration;

import com.amit.common.util.DaoTestHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

@Configuration
@ComponentScan(basePackages = {
        "com.amit.post.repository"
})
public class DaoTestConfiguration {

    @Bean
    public DataSource dataSource(
            @Value("${test.jdbc.url}") String url,
            @Value("${test.jdbc.username}") String username,
            @Value("${test.jdbc.password}") String password
    ) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }

    @Bean
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate(DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @Bean
    public DaoTestHelper daoTestHelper(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        return new DaoTestHelper(namedParameterJdbcTemplate);
    }

    @EventListener
    public void populate(ContextRefreshedEvent contextRefreshedEvent) {
        DataSource dataSource = contextRefreshedEvent.getApplicationContext().getBean(DataSource.class);
        ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator();
        resourceDatabasePopulator.addScript(new ClassPathResource("schema.sql"));
        resourceDatabasePopulator.addScript(new ClassPathResource("tables.sql"));
        resourceDatabasePopulator.execute(dataSource);
    }

}
