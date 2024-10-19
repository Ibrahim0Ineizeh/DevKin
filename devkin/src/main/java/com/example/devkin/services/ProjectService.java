package com.example.devkin.services;

import com.example.devkin.dtos.CreateProjectDto;
import com.example.devkin.dtos.ProjectDto;
import com.example.devkin.entities.Project;
import com.example.devkin.entities.User;
import com.example.devkin.mappings.ProjectMapper;
import com.example.devkin.repositories.ProjectRepository;
import com.example.devkin.repositories.UserRepository;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.MinioException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private FileStorageService fileStorageService;

    @Transactional
    public ProjectDto createProject(CreateProjectDto project) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        User currentUser = userRepository.findByEmail(user.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (projectRepository.existsByOwnerAndName(currentUser, project.getName())) {
            throw new IllegalArgumentException("A project with the same name already exists.");
        }

        String projectUrl = String.format("projects/%d/%s/", currentUser.getId(), project.getName());

        Project temp = new Project();
        temp.setName(project.getName());
        temp.setDescription(project.getDescription());
        temp.setLanguage(project.getLanguage());
        temp.setOwner(currentUser);
        temp.setSize(0);
        temp.setUrl(projectUrl);
        temp.setCreatedAt(LocalDateTime.now());
        temp.setLastModified(LocalDateTime.now());
        Project savedProject = projectRepository.save(temp);

        try {
            fileStorageService.createFolder(projectUrl, temp.getName(), temp.getProjectId());
        } catch (Exception e) {
            // Handle the exception, optionally roll back the transaction
            // Consider deleting the saved project if folder creation fails
            projectRepository.delete(savedProject);
            throw new RuntimeException("Failed to create project folder", e);
        }

        return projectMapper.toProjectDto(savedProject);
    }


    @Transactional(readOnly = true)
    public ProjectDto getProjectById(Integer projectId) {
        Optional<Project> projectOptional = projectRepository.findById(projectId);

        if (projectOptional.isEmpty()) {
            return null;
        }

        Project project = projectOptional.get();
        return projectMapper.toProjectDto(project);  // Return the DTO
    }

    @Transactional(readOnly = true)
    public List<ProjectDto> getAllProjects() {
        List<Project> projects = projectRepository.findAll();

        return projects.stream().map(project -> {
            return projectMapper.toProjectDto(project);
        }).collect(Collectors.toList());
    }

    @Transactional
    public ProjectDto updateProject(Integer projectId, CreateProjectDto updatedProjectData) {
        Optional<Project> projectOptional = projectRepository.findById(projectId);

        if (projectOptional.isPresent()) {
            Project existingProject = projectOptional.get();

            String oldFolderPath = existingProject.getUrl();
            String newFolderPath = String.format("projects/%d/%s/", existingProject.getOwner().getId(), updatedProjectData.getName());

            existingProject.setName(updatedProjectData.getName());
            existingProject.setDescription(updatedProjectData.getDescription());
            existingProject.setLanguage(updatedProjectData.getLanguage());
            existingProject.setLastModified(LocalDateTime.now());

            projectRepository.save(existingProject);

            try {
                fileStorageService.updateFolder(oldFolderPath, newFolderPath);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

            return  projectMapper.toProjectDto(existingProject);
        }
        return null;  // Return null if the project doesn't exist
    }

    @Transactional
    public boolean deleteProject(Integer projectId) {
        if (projectRepository.existsById(projectId)) {
            Project project = projectRepository.findById(projectId).orElse(null);
            if (project != null) {
                String folderPath = project.getUrl();
                try {
                    fileStorageService.deleteFolder(folderPath);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to delete project folder from MinIO", e);
                }
                projectRepository.deleteById(projectId);
                return true;
            }
        }
        return false;
    }
}