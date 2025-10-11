package com.amit.common.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@ComponentScan(
        basePackages = "com.amit",
        useDefaultFilters = false,
        includeFilters = {
                @ComponentScan.Filter(RestController.class),
                @ComponentScan.Filter(ControllerAdvice.class)
        }
)
public class WebConfiguration {
}
