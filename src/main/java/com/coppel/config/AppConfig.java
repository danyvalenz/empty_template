package com.coppel.config;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AppConfig
 */
@Component
@ConfigurationProperties(prefix = "app")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AppConfig {

    @Autowired
    private BuildProperties buildProperties;

    @Value("${server.servlet.context-path}")
    private String contextVersion;

    private String authUri;
    private boolean ignoreSession;
    private boolean useProxy;
    private String environment;
    private String allowedOrigins;
    private String allowedMethods;
    private String allowedHeaders;
    private String exposedHeaders;

    public String compatibilityContextVersion() {
        int position = this.contextVersion.toLowerCase(Locale.ROOT).indexOf("/v", 0) + 2;
        String semanticContextVersion = this.contextVersion.substring(position);
        String majorVersionContext = semanticContextVersion.split("[.]")[0];
        String majorVersion = this.buildProperties.getVersion() == null ?
                majorVersionContext : this.buildProperties.getVersion().split("[.]")[0];

        if (majorVersionContext.equals(majorVersion)) {
            return this.buildProperties.getVersion();
        } else {
            return String.format("Major project version in pom.xml (%s), differs from major context version (%s)", majorVersion, semanticContextVersion);
        }
    }
}
