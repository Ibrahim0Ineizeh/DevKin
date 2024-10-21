package com.example.devkin.controllers;

import com.example.devkin.dtos.ExecuteDto;
import com.example.devkin.dtos.ExecutionResultDto;
import com.example.devkin.services.ExecutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard/project/code")
public class ExecutionController {

    @Autowired
    private ExecutionService executionService;

    @PostMapping("/execute")
    public ResponseEntity<?> executeCode(@RequestBody ExecuteDto executeDto) {
        try {
            String result = executionService.executeCode(executeDto.getLanguage(), executeDto.getCode());
            return ResponseEntity.ok(new ExecutionResultDto("Success",result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ExecutionResultDto("Failed",e.getMessage()));
        }
    }
}
