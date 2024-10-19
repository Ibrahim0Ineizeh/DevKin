package com.example.devkin.controllers;

import com.example.devkin.dtos.CreateProjectDto;
import com.example.devkin.dtos.ProjectDto;
import com.example.devkin.entities.Project;
import com.example.devkin.services.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.StyledEditorKit;
import java.util.List;

@RequestMapping("/dashboard/project")
@RestController
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @PostMapping("/create")
    public ResponseEntity<?> createProject(@RequestBody CreateProjectDto project){
        try {
            ProjectDto createdProject = projectService.createProject(project);
            return new ResponseEntity<>(createdProject, HttpStatus.CREATED);
        } catch (UsernameNotFoundException e) {
            // Handle user not found
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException e) {
            // Handle data integrity violation (e.g., duplicate project name)
            return new ResponseEntity<>("Data integrity violation: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // Log the error for debugging purposes
            // You can use a logger here
            e.printStackTrace(); // Consider using a proper logger
            return new ResponseEntity<>("An error occurred while creating the project", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get a project by its ID
    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectDto> getProjectById(@PathVariable Integer projectId) {
        ProjectDto project = projectService.getProjectById(projectId);
        if (project != null) {
            return new ResponseEntity<>(project, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Get all projects
    @GetMapping
    public ResponseEntity<List<ProjectDto>> getAllProjects() {
        try {
            List<ProjectDto> projects = projectService.getAllProjects();
            return new ResponseEntity<>(projects, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Update a project's information
    @PutMapping("/{projectId}")
    public ResponseEntity<ProjectDto> updateProject(
            @PathVariable Integer projectId,
            @RequestBody CreateProjectDto projectDetails) {
        ProjectDto updatedProject = projectService.updateProject(projectId, projectDetails);
        if (updatedProject != null) {
            return new ResponseEntity<>(updatedProject, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Delete a project
    @DeleteMapping("/{projectId}")
    public ResponseEntity<Boolean> deleteProject(@PathVariable Integer projectId) {
        try {
            return new ResponseEntity<>( projectService.deleteProject(projectId),HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(false,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
