package com.bartheme.githubscanner.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${github.api.url}")
    private String githubBaseUrl;

    @Value("${github.api.version}")
    private String githubApiVersion;

    @Value("${github.token}")
    private String githubToken;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(githubBaseUrl)
                .defaultHeader("Accept", "application/vnd.github+json")
                .defaultHeader("Authorization", githubToken.isEmpty() ? "" : "Bearer " + githubToken)
                .defaultHeader("X-GitHub-Api-Version", githubApiVersion)
                .build();
    }
}
