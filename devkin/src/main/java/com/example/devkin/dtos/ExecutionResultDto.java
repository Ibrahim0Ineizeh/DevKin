package com.example.devkin.dtos;

public class ExecutionResultDto {
    private String status;
    private String output;

    public ExecutionResultDto(String status, String output) {
        this.status = status;
        this.output = output;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
