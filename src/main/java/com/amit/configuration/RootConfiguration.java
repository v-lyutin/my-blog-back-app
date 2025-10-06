package com.amit.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.RestController;

@Configuration
@EnableTransactionManagement
@ComponentScan(
        basePackages = {
                "com.amit.configuration",
                "com.amit.post",
                "com.amit.comment"
        },
        excludeFilters = @ComponentScan.Filter(RestController.class))
public class RootConfiguration {
}
