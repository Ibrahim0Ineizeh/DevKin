package com.example.devkin.dtos;

public class ExecutionRequestsDto {
    private String sessionId;
    private ExecuteDto executeDto;

    public ExecutionRequestsDto(String sessionId, ExecuteDto executeDto) {
        this.sessionId = sessionId;
        this.executeDto = executeDto;
    }

    public String getSessionId() {
        return sessionId;
    }

    public ExecuteDto getCodeRequest() {
        return executeDto;
    }
}
