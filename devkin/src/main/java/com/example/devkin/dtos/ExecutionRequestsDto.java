package com.example.devkin.dtos;

public class ExecutionRequestsDto {
    private String projectSlug;
    private ExecuteDto executeDto;

    public ExecutionRequestsDto(String projectSlug, ExecuteDto executeDto) {
        this.projectSlug = projectSlug;
        this.executeDto = executeDto;
    }

    public String getProjectSlug() {
        return projectSlug;
    }

    public ExecuteDto getCodeRequest() {
        return executeDto;
    }
}
