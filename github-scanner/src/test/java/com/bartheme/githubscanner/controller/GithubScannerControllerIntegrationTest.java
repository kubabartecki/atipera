package com.bartheme.githubscanner.controller;

import com.bartheme.githubscanner.GithubScannerApplication;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;

import static com.bartheme.githubscanner.util.JsonFileReader.readJsonFile;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.hamcrest.Matchers.hasSize;


@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = GithubScannerApplication.class)
@AutoConfigureWireMock(port = 8088)
@TestPropertySource(
        locations = "classpath:application.properties",
        properties = """
                github.api.url=http://localhost:8088
                github.api.url.per_page=2
                github.token=
                """
)
class GithubScannerControllerIntegrationTest {
    private final String appUrlPrefix = "/api/v1";
    private final String githubApiVersion = "2022-11-28";
    private final String username = "kubabartecki";
    private final int perPage = 2;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private WebTestClient webTestClient;

    @Test
    void getUserRepositories_EmptyList() {
        // given
        stubFor(get(urlEqualTo(String.format("/users/%s/repos?type=all&per_page=%d", username, perPage)))
                .withHeader("Accept", equalTo("application/vnd.github+json"))
                .withHeader("X-GitHub-Api-Version", equalTo(githubApiVersion))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody("[]")));

        // when & then
        webTestClient.get()
                .uri(String.format("%s/%s/repositories", appUrlPrefix, username))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody().json("[]");
    }

    @Test
    void getUserRepositories_TwoPagesOfRepositories() throws IOException {
        // given
        String linkHeader = String.format(
                "<http://localhost:8088/users/%s/repos?type=all&per_page=%d&page=2>; rel=\"next\"," +
                        "<http://localhost:8088/users/%s/repos?type=all&per_page=%d&page=2>; rel=\"last\"",
                username, perPage, username, perPage);
        stubFor(get(urlEqualTo(String.format("/users/%s/repos?type=all&per_page=%d", username, perPage)))
                .withHeader("Accept", equalTo("application/vnd.github+json"))
                .withHeader("X-GitHub-Api-Version", equalTo(githubApiVersion))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withHeader(HttpHeaders.LINK, linkHeader)
                        .withBody(readJsonFile("response/github-api/repository/200_RepoList_PerPage2_Page1.json"))));

        String linkHeader2 = String.format(
                "<http://localhost:8088/users/%s/repos?type=all&per_page=%d&page=1>; rel=\"prev\"," +
                        "<http://localhost:8088/users/%s/repos?type=all&per_page=%d&page=1>; rel=\"first\"",
                username, perPage, username, perPage);
        stubFor(get(urlEqualTo(String.format("/users/%s/repos?type=all&per_page=%d&page=2", username, perPage)))
                .withHeader("Accept", equalTo("application/vnd.github+json"))
                .withHeader("X-GitHub-Api-Version", equalTo(githubApiVersion))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withHeader(HttpHeaders.LINK, linkHeader2)
                        .withBody(readJsonFile("response/github-api/repository/200_RepoList_PerPage2_Page2.json"))));

        stubFor(get(urlMatching(String.format("/repos/%s/(.*)/branches\\?per_page=%d", username, perPage)))
                .withHeader("Accept", equalTo("application/vnd.github+json"))
                .withHeader("X-GitHub-Api-Version", equalTo(githubApiVersion))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody(readJsonFile("response/github-api/branch/200_BranchList_Single.json"))));

        // when & then
        webTestClient.get()
                .uri(String.format("%s/%s/repositories", appUrlPrefix, username))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$").value(hasSize(3))
                .consumeWith(response -> {
                    try {
                        String actualJson = objectMapper.readTree(response.getResponseBody()).toString();
                        String expectedJson = readJsonFile("response/app-api/200_RepositoryDtoList_ManyRepositories.json");

                        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT);
                    } catch (Exception e) {
                        throw new RuntimeException("Error processing JSON", e);
                    }
                });
    }

    @Test
    void getUserRepositories_TwoPagesOfBranches() throws IOException {
        // given
        stubFor(get(urlEqualTo(String.format("/users/%s/repos?type=all&per_page=%d", username, perPage)))
                .withHeader("Accept", equalTo("application/vnd.github+json"))
                .withHeader("X-GitHub-Api-Version", equalTo(githubApiVersion))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody(readJsonFile("response/github-api/repository/200_RepoList_PerPage2_Page2.json"))));

        String linkHeader = String.format(
                "<http://localhost:8088/repos/%s/x/branches?per_page=%d&page=2>; rel=\"next\"," +
                        "<http://localhost:8088/repos/%s/x/branches?per_page=%d&page=2>; rel=\"last\"",
                username, perPage, username, perPage);
        stubFor(get(urlMatching(String.format("/repos/%s/(.*)/branches\\?per_page=%d", username, perPage)))
                .withHeader("Accept", equalTo("application/vnd.github+json"))
                .withHeader("X-GitHub-Api-Version", equalTo(githubApiVersion))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withHeader(HttpHeaders.LINK, linkHeader)
                        .withBody(readJsonFile("response/github-api/branch/200_BranchList_PerPage2_Page1.json"))));

        String linkHeader2 = String.format(
                "<http://localhost:8088/repos/%s/x/branches?per_page=%d&page=1>; rel=\"prev\"," +
                        "<http://localhost:8088/repos/%s/x/branches?per_page=%d&page=1>; rel=\"first\"",
                username, perPage, username, perPage);
        stubFor(get(urlMatching(String.format("/repos/%s/(.*)/branches\\?per_page=%d&page=2", username, perPage)))
                .withHeader("Accept", equalTo("application/vnd.github+json"))
                .withHeader("X-GitHub-Api-Version", equalTo(githubApiVersion))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withHeader(HttpHeaders.LINK, linkHeader2)
                        .withBody(readJsonFile("response/github-api/branch/200_BranchList_PerPage2_Page2.json"))));

        // when & then
        webTestClient.get()
                .uri(String.format("%s/%s/repositories", appUrlPrefix, username))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$").value(hasSize(1))
                .consumeWith(response -> {
                    try {
                        String actualJson = objectMapper.readTree(response.getResponseBody()).toString();
                        String expectedJson = readJsonFile("response/app-api/200_RepositoryDtoList_ManyBranches.json");

                        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT);
                    } catch (Exception e) {
                        throw new RuntimeException("Error processing JSON", e);
                    }
                });
    }

    @Test
    void getUserRepositories_UserNotFound() throws IOException {
        // given
        stubFor(get(urlEqualTo(String.format("/users/%s/repos?type=all&per_page=%d", username, perPage)))
                .withHeader("Accept", equalTo("application/vnd.github+json"))
                .withHeader("X-GitHub-Api-Version", equalTo(githubApiVersion))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.NOT_FOUND.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody(readJsonFile("response/github-api/repository/404_UserNotFound.json"))));

        // when & then
        webTestClient.get()
                .uri(String.format("%s/%s/repositories", appUrlPrefix, username))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Could not find user with given username")
                .jsonPath("$.status").isEqualTo(404);
    }
}
