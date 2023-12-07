package com.coppel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.coppel.config.AppConfig;

import java.util.Properties;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class MainApplication {

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }

    @Bean @ConditionalOnMissingBean(BuildProperties.class)
    BuildProperties buildProperties() {
        return new BuildProperties(new Properties());
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Autowired
            private AppConfig config;

            @Override
            public void addCorsMappings(CorsRegistry registry) {
                String regex = "\\s*,\\s*";

                registry.addMapping("/**")
                        .allowedOrigins(config.getAllowedOrigins().split(regex))
                        .allowedMethods(config.getAllowedMethods().split(regex))
                        .allowedHeaders(config.getAllowedHeaders().split(regex))
                        .exposedHeaders(config.getExposedHeaders().split(regex));
            }
        };
    }

}
