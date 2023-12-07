package com.coppel.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "proxy")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProxyConfig {

    private String ip;
    private String port;
}
