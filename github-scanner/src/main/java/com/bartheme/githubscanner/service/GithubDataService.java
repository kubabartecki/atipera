package com.bartheme.githubscanner.service;

import com.bartheme.githubscanner.model.github.GithubApiBranchResponse;
import com.bartheme.githubscanner.model.github.GithubApiRepositoryResponse;
import reactor.core.publisher.Flux;

public interface GithubDataService {
    Flux<GithubApiRepositoryResponse> getGithubUserRepositories(String username);

    Flux<GithubApiBranchResponse> getGithubRepositoryBranches(GithubApiRepositoryResponse githubRepository);
}
