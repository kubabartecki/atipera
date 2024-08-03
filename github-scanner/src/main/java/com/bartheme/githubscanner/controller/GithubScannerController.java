package com.bartheme.githubscanner.controller;

import com.bartheme.githubscanner.model.RepositoryDto;
import com.bartheme.githubscanner.service.GithubScannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class GithubScannerController {

    private final GithubScannerService githubScannerService;

    @GetMapping(value = "{username}/repositories", produces = "application/json")
    public Flux<RepositoryDto> getUserRepositories(@PathVariable("username") String username) {
        return githubScannerService.getUserRepositories(username);
    }
}
