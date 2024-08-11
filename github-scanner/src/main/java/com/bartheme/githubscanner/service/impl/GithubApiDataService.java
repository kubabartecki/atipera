package com.bartheme.githubscanner.service.impl;

import com.bartheme.githubscanner.exception.UserNotFoundException;
import com.bartheme.githubscanner.model.github.GithubApiBranchResponse;
import com.bartheme.githubscanner.model.github.GithubApiRepositoryResponse;
import com.bartheme.githubscanner.service.GithubDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class GithubApiDataService implements GithubDataService {
    private static final Pattern NEXT_LINK_PATTERN = Pattern.compile("(?<=<)([\\S]*)(?=>; rel=\"next\")");
    private final WebClient webClient;

    @Override
    public Flux<GithubApiRepositoryResponse> getGithubUserRepositories(String username) {
        return fetchRepos(
                UriComponentsBuilder.fromUriString("/users/{username}/repos")
                        .queryParam("type", "all")
                        .queryParam("per_page", "100")
                        .build(username)
                        .toString()
        );
    }

    private Flux<GithubApiRepositoryResponse> fetchRepos(String url) {
        log.debug("Fetching data from {}", url);
        return webClient.get()
                .uri(url)
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals,
                        response -> Mono.error(new UserNotFoundException()))
                .toEntityList(GithubApiRepositoryResponse.class)
                .flatMapMany(response -> {
                    List<GithubApiRepositoryResponse> repos = response.getBody();
                    String nextUrl = getNextPageUrl(response.getHeaders().getFirst("Link"));

                    if (nextUrl != null) {
                        return Flux.concat(Flux.fromIterable(repos), fetchRepos(nextUrl));
                    } else {
                        return Flux.fromIterable(repos);
                    }
                })
                .filter(repository -> !repository.isFork());
    }

    @Override
    public Flux<GithubApiBranchResponse> getGithubRepositoryBranches(GithubApiRepositoryResponse githubRepository) {
        return fetchBranches(
                UriComponentsBuilder.fromUriString("/repos/{owner}/{repositoryName}/branches")
                        .queryParam("per_page", "100")
                        .build(githubRepository.ownerLogin(), githubRepository.name())
                        .toString()
        );
    }

    private Flux<GithubApiBranchResponse> fetchBranches(String url) {
        return webClient.get()
                .uri(url)
                .retrieve()
                .toEntityList(GithubApiBranchResponse.class)
                .flatMapMany(response -> {
                    List<GithubApiBranchResponse> branches = response.getBody();
                    String nextUrl = getNextPageUrl(response.getHeaders().getFirst("Link"));

                    if (nextUrl != null) {
                        return Flux.concat(Flux.fromIterable(branches), fetchBranches(nextUrl));
                    } else {
                        return Flux.fromIterable(branches);
                    }
                });
    }

    private String getNextPageUrl(String linkHeader) {
        if (linkHeader != null && linkHeader.contains("rel=\"next\"")) {
            Matcher matcher = NEXT_LINK_PATTERN.matcher(linkHeader);
            if (matcher.find()) {
                return matcher.group();
            }
        }
        return null;
    }
}
