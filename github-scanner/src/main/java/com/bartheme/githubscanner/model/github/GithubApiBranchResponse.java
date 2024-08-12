package com.bartheme.githubscanner.model.github;

import com.bartheme.githubscanner.model.github.deserializer.GithubApiBranchResponseDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = GithubApiBranchResponseDeserializer.class)
public record GithubApiBranchResponse(String name, String lastCommitSha) {
}
