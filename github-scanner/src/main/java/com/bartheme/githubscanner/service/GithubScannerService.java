package com.bartheme.githubscanner.service;

import com.bartheme.githubscanner.model.BranchDto;
import com.bartheme.githubscanner.model.RepositoryDto;
import com.bartheme.githubscanner.model.github.GithubApiRepositoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class GithubScannerService {
    private final GithubDataService githubDataService;

    public Flux<RepositoryDto> getUserRepositories(String username) {
        Flux<GithubApiRepositoryResponse> githubApiRepositoryResponse =
                githubDataService.getGithubUserRepositories(username);

        return githubApiRepositoryResponse.flatMap(githubRepository ->
                githubDataService.getGithubRepositoryBranches(githubRepository)
                        .map(githubBranch -> BranchDto.builder()
                                .name(githubBranch.name())
                                .lastCommitSha(githubBranch.lastCommitSha())
                                .build())
                        .collectList()
                        .map(branches -> RepositoryDto.builder()
                                .name(githubRepository.name())
                                .ownerLogin(githubRepository.ownerLogin())
                                .branches(branches)
                                .build())
                        .subscribeOn(Schedulers.parallel())
        );
    }
}
