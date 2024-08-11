package com.bartheme.githubscanner.model.github.deserializer;

import com.bartheme.githubscanner.model.github.GithubApiRepositoryResponse;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class GithubApiRepositoryResponseDeserializer extends StdDeserializer<GithubApiRepositoryResponse> {

    public GithubApiRepositoryResponseDeserializer() {
        this(null);
    }

    public GithubApiRepositoryResponseDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public GithubApiRepositoryResponse deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);
        String name = node.get("name").asText();
        boolean isFork = node.get("fork").asBoolean();
        String ownerLogin = node.get("owner").get("login").asText();
        return new GithubApiRepositoryResponse(name, isFork, ownerLogin);
    }
}

