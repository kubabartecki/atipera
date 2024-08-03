package com.bartheme.githubscanner.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RepositoryDto {
    private String name;
    private String ownerLogin;
    private List<BranchDto> branches;
}

