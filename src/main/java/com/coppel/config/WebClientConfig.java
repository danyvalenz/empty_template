package com.coppel.config;

import com.coppel.webclient.WebClientBeansRegistration;
import lombok.Getter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@Getter
public class WebClientConfig {

    private final int i;

    public WebClientConfig() {
        i = 1;
    }

    @Bean
    public static WebClientBeansRegistration webClientBeansRegistration(Environment environment, WebClient.Builder builder) {
        return new WebClientBeansRegistration(environment, builder);
    }

}
