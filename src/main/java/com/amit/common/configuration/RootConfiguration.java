package com.amit.common.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestController;

@Configuration
@EnableTransactionManagement
@ComponentScan(
        basePackages = "com.amit",
        excludeFilters = {
                @ComponentScan.Filter(RestController.class),
                @ComponentScan.Filter(ControllerAdvice.class)
        })
public class RootConfiguration {
}
