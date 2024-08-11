package com.bartheme.githubscanner.model;

import lombok.Builder;

import java.util.List;

@Builder
public record RepositoryDto(String name, String ownerLogin, List<BranchDto> branches) {
}
