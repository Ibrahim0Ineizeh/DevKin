package com.example.devkin.services;

import com.example.devkin.dtos.*;
import com.example.devkin.entities.File;
import com.example.devkin.entities.Folder;
import com.example.devkin.entities.Project;
import com.example.devkin.entities.User;
import com.example.devkin.mappings.ProjectMapper;
import com.example.devkin.repositories.ProjectRepository;
import com.example.devkin.repositories.UserRepository;
import com.example.devkin.utils.SlugUtil;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;
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

    @Autowired
    private SlugUtil slugUtil;

    @Transactional
    public ProjectDto createProject(CreateProjectDto project) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        User currentUser = userRepository.findByEmail(user.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String projectUrl = String.format("projects/%d/%s/", currentUser.getId(), project.getName());

        Project temp = new Project();
        temp.setName(project.getName());
        temp.setDescription(project.getDescription());
        temp.setLanguage(project.getLanguage());
        temp.setOwner(currentUser);
        temp.setSize(0);
        temp.setSlug(slugUtil.generateUniqueSlug(project.getName()));
        temp.setUrl(projectUrl);
        temp.setCreatedAt(LocalDateTime.now());
        temp.setLastModified(LocalDateTime.now());
        Project savedProject = projectRepository.save(temp);

        try {
            fileStorageService.createFolder(projectUrl, temp.getName(), temp.getProjectId());
        } catch (Exception e) {
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
        return projectMapper.toProjectDto(project);
    }

    @Transactional(readOnly = true)
    public List<ProjectDto> getAllProjects() {
        List<Project> projects = projectRepository.findAll();

        return projects.stream().map(project -> {
            return projectMapper.toProjectDto(project);
        }).collect(Collectors.toList());
    }

    @Transactional
    public ProjectDto updateProject(Integer projectId, SlugProjectDto updatedProjectData) {
        Optional<Project> projectOptional = projectRepository.findById(projectId);

        if (projectOptional.isPresent()) {
            Project existingProject = projectOptional.get();

            String oldFolderPath = existingProject.getUrl();
            String newFolderPath = String.format("projects/%d/%s/", existingProject.getOwner().getId(), existingProject.getName());

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
        return null;
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

    @Transactional(readOnly = true)
    public List<ProjectDto> getProjectsByUserEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Fetch all projects where the user is either the owner or part of the developers
        List<Project> projects = projectRepository.findByOwnerOrDeveloper(user);

        return projects.stream()
                .map(project -> projectMapper.toProjectDto(project))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FileStructureDto> getProjectFiles(Integer projectId) {
        Optional<Project> projectOptional = projectRepository.findById(projectId);
        if (projectOptional.isEmpty()) {
            return Collections.emptyList(); // or throw an exception
        }

        Project project = projectOptional.get();

        // Map all files in the project
        List<FileStructureDto> fileStructureDtos = project.getFiles().stream()
                .map(file -> {
                    FileStructureDto fileStructureDto = new FileStructureDto();
                    fileStructureDto.setFileId(file.getFileId());
                    fileStructureDto.setFileName(file.getFileName());
                    fileStructureDto.setFilePath(file.getFilePath());
                    fileStructureDto.setFileType(file.getFileType());
                    fileStructureDto.setFileSize(file.getFileSize());
                    fileStructureDto.setLastModefied(file.getLastModified());
                    return fileStructureDto;
                })
                .collect(Collectors.toList());

        return fileStructureDtos;
    }

    public Integer getProjectBySlug(String slug) {
        Optional<Project> project = projectRepository.findBySlug(slug);
        return project != null ? project.get().getProjectId(): null;
    }
}