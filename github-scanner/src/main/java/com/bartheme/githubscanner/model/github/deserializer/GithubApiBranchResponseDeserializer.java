package com.bartheme.githubscanner.model.github.deserializer;

import com.bartheme.githubscanner.model.github.GithubApiBranchResponse;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class GithubApiBranchResponseDeserializer extends StdDeserializer<GithubApiBranchResponse> {

    public GithubApiBranchResponseDeserializer() {
        this(null);
    }

    public GithubApiBranchResponseDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public GithubApiBranchResponse deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);
        String name = node.get("name").asText();
        String lastCommitSha = node.get("commit").get("sha").asText();
        return new GithubApiBranchResponse(name, lastCommitSha);
    }
}
