package com.bartheme.githubscanner.model.github;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class GithubApiBranchResponse {
    private String name;
    private String lastCommitSha;

    @SuppressWarnings("unchecked")
    @JsonProperty("commit")
    private void unpackNestedCommit(Map<String, Object> commit) {
        this.lastCommitSha = (String) commit.get("sha");
    }
}
