package com.bartheme.githubscanner.model.github;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class GithubApiRepositoryResponse {
    private String name;
    @JsonProperty("fork")
    private boolean isFork;
    private String ownerLogin;

    @SuppressWarnings("unchecked")
    @JsonProperty("owner")
    private void unpackNestedOwner(Map<String, Object> owner) {
        this.ownerLogin = (String) owner.get("login");
    }
}
