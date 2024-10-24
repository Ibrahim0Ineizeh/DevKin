package com.example.devkin.controllers;

import com.example.devkin.dtos.ExecuteDto;
import com.example.devkin.dtos.ExecutionResultDto;
import com.example.devkin.dtos.FileDto;
import com.example.devkin.entities.Project;
import com.example.devkin.repositories.ProjectRepository;
import com.example.devkin.services.ExecutionService;
import com.example.devkin.services.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/dashboard/project/code")
public class ExecutionController {

    @Autowired
    private ExecutionService executionService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/execute")
    public ResponseEntity<?> executeCode(@RequestBody ExecuteDto executeDto) {
        try {
            byte[] fileData = fileStorageService.getFileContents(fileStorageService.calcFilePath(executeDto.getFileDto()));
            String code = new String(fileData, StandardCharsets.UTF_8);
            String result = executionService.executeCode(executeDto.getLanguage(), code);
            return ResponseEntity.ok(new ExecutionResultDto("Success",result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ExecutionResultDto("Failed",e.getMessage()));
        }
    }
}
