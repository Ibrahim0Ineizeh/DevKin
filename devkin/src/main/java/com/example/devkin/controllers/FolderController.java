package com.example.devkin.controllers;

import com.example.devkin.dtos.FolderDto;
import com.example.devkin.entities.Project;
import com.example.devkin.repositories.ProjectRepository;
import com.example.devkin.services.FileStorageService;
import com.example.devkin.services.ProjectService; // Assuming you have a service to fetch project details
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dashboard/project/folder")
public class FolderController {

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private ProjectRepository projectRepository;

    // Create a folder inside a project
    @PostMapping("/create")
    public ResponseEntity<String> createFolder(@RequestBody FolderDto folderDto) {
        try {
            Project project = projectRepository.findBySlug(folderDto.getProjectSlug()).get();
            Integer ownerId = project.getOwner().getId();
            Integer projectId = project.getProjectId();

            // Set the folder path
            String folderPath = "projects/" + ownerId + "/" + project.getName() + "/" + folderDto.getFolderName() + "/";
            fileStorageService.createFolder(folderPath, folderDto.getFolderName(), projectId);
            return new ResponseEntity<>("Folder created successfully", HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace(); // Use proper logging in production
            return new ResponseEntity<>("Failed to create folder: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateFolder(@RequestBody FolderDto folderDto) {
        try {
            Project project = projectRepository.findBySlug(folderDto.getProjectSlug()).get();
            Integer ownerId = project.getOwner().getId();
            Integer projectId = project.getProjectId();

            String oldFolderPath = "projects/" + ownerId + "/" + project.getName() + "/" + folderDto.getFolderName() + "/";
            String newFolderPath = "projects/" + ownerId + "/" + project.getName() + "/" + folderDto.getNewFolderName() + "/";

            fileStorageService.updateFolder(oldFolderPath, newFolderPath);

            return new ResponseEntity<>("Folder name updated successfully", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace(); // Use proper logging in production
            return new ResponseEntity<>("Failed to update folder name: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteFolder(@RequestBody FolderDto folderDto) {
        try {
            Project project = projectRepository.findBySlug(folderDto.getProjectSlug()).get();
            Integer ownerId = project.getOwner().getId();

            String folderPath = "projects/" + ownerId + "/" + project.getName() + "/" + folderDto.getFolderName() + "/";
            fileStorageService.deleteFolder(folderPath);

            return new ResponseEntity<>("Folder deleted successfully", HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            e.printStackTrace(); // Use proper logging in production
            return new ResponseEntity<>("Failed to delete folder: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
