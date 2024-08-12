package com.bartheme.githubscanner.model;

import lombok.Builder;

@Builder
public record BranchDto(String name, String lastCommitSha) {
}
