package com.bartheme.githubscanner.model.github;

import com.bartheme.githubscanner.model.github.deserializer.GithubApiRepositoryResponseDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = GithubApiRepositoryResponseDeserializer.class)
public record GithubApiRepositoryResponse(String name, boolean isFork, String ownerLogin) {
}
