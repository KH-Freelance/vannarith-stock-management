package com.hfsolution.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // Allow all paths
                .allowedOrigins("*") // Change to your specific origin for production
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Allow specific methods
                .allowedHeaders("*"); // Allow all headers
            }
        };
    }
}