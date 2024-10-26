package com.example.devkin.controllers;

import com.example.devkin.dtos.ExecutionResultDto;
import com.example.devkin.dtos.FileDto;
import com.example.devkin.entities.Project;
import com.example.devkin.repositories.ProjectRepository;
import com.example.devkin.services.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/dashboard/project/files")
public class FileController {

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(
            @RequestParam("file") MultipartFile file,
            @ModelAttribute FileDto fileDto) {
        try {
            Project project = projectRepository.findBySlug(fileDto.getProjectSlug()).get();
            Integer ownerId = project.getOwner().getId();
            Integer projectId = project.getProjectId();

            String filePath = "projects/" + ownerId + "/" + project.getName() +
                    (fileDto.getFilePath().isEmpty() ? "" : "/" + fileDto.getFilePath()) +
                    "/";

            fileStorageService.createFileInFolder(
                    filePath,
                    file.getOriginalFilename(),
                    file.getBytes(),
                    file.getContentType(),
                    file.getSize(),
                    projectId
            );

            messagingTemplate.convertAndSend("/topic/change/" + fileDto.getProjectSlug(),
                    new ExecutionResultDto("COMPLETED", filePath));

            return new ResponseEntity<>("File uploaded successfully", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to upload file", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update-contents")
    public ResponseEntity<String> updateFileContents(
            @RequestParam("file") MultipartFile file,
            @ModelAttribute FileDto fileDto) {
        try {
            Project project = projectRepository.findBySlug(fileDto.getProjectSlug()).get();
            Integer ownerId = project.getOwner().getId();

            String filePath = "projects/" + ownerId + "/" + project.getName() +
                    (fileDto.getFilePath().isEmpty() ? "" : "/" + fileDto.getFilePath()) +
                    "/" + fileDto.getFileName();
            fileStorageService.updateFileContents(filePath, file.getBytes(), file.getContentType());

            messagingTemplate.convertAndSend("/topic/change/" + fileDto.getProjectSlug(),
                    new ExecutionResultDto("COMPLETED", filePath));

            return new ResponseEntity<>("File contents updated successfully", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to update file contents", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/rename")
    public ResponseEntity<String> renameFile(@RequestBody FileDto fileDto) {
        try {
            Project project = projectRepository.findBySlug(fileDto.getProjectSlug()).get();
            Integer ownerId = project.getOwner().getId();

            String filePath = "projects/" + ownerId + "/" + project.getName() +
                    (fileDto.getFilePath().isEmpty() ? "" : "/" + fileDto.getFilePath()) +
                    "/" + fileDto.getFileName();
            fileStorageService.updateFileName(filePath, fileDto.getNewFileName());

            messagingTemplate.convertAndSend("/topic/change/" + fileDto.getProjectSlug(),
                    new ExecutionResultDto("COMPLETED", filePath));

            return new ResponseEntity<>("File renamed successfully", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to rename file", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/get-contents")
    public ResponseEntity<byte[]> getFileContents(@RequestBody FileDto fileDto) {
        try {
            Project project = projectRepository.findBySlug(fileDto.getProjectSlug()).orElseThrow(() -> new RuntimeException("Project not found"));
            Integer ownerId = project.getOwner().getId();

            String filePath = "projects/" + ownerId + "/" + project.getName() +
                    (fileDto.getFilePath().isEmpty() ? "" : "/" + fileDto.getFilePath()) +
                    "/" + fileDto.getFileName();

            byte[] fileData = fileStorageService.getFileContents(filePath);
            String contentType = fileStorageService.getFileContentType(filePath);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            headers.setContentLength(fileData.length);

            messagingTemplate.convertAndSend("/topic/change/" + fileDto.getProjectSlug(),
                    new ExecutionResultDto("COMPLETED", filePath));

            return new ResponseEntity<>(fileData, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteFile(@RequestBody FileDto fileDto) {
        try {
            Project project = projectRepository.findBySlug(fileDto.getProjectSlug()).get();
            Integer ownerId = project.getOwner().getId();

            String filePath = "projects/" + ownerId + "/" + project.getName() + "/" + fileDto.getFileName();
            fileStorageService.deleteFile(filePath);

            messagingTemplate.convertAndSend("/topic/change/" + fileDto.getProjectSlug(),
                    new ExecutionResultDto("COMPLETED", filePath));

            return new ResponseEntity<>("File deleted successfully", HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            e.printStackTrace(); // Use proper logging in production
            return new ResponseEntity<>("Failed to delete file", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
