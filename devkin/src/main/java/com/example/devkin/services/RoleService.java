package com.example.devkin.services;

import com.example.devkin.entities.Project;
import com.example.devkin.entities.ProjectDeveloperRole;
import com.example.devkin.entities.ProjectDeveloperId;
import com.example.devkin.entities.User;
import com.example.devkin.repositories.ProjectRepository;
import com.example.devkin.repositories.ProjectDeveloperRoleRepository;
import com.example.devkin.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class RoleService {
    @Autowired
    private ProjectDeveloperRoleRepository projectDeveloperRoleRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public ProjectDeveloperRole assignRole(Integer projectId, Integer developerId, String role) {
        Optional<Project> project = projectRepository.findById(projectId);
        Optional<User> developer = userRepository.findById(developerId);

        if (project.isPresent() && developer.isPresent()) {
            ProjectDeveloperRole projectDeveloperRole = new ProjectDeveloperRole();
            ProjectDeveloperId id = new ProjectDeveloperId(projectId, developerId);
            projectDeveloperRole.setId(id);
            projectDeveloperRole.setProject(project.get());
            projectDeveloperRole.setDeveloper(developer.get());
            projectDeveloperRole.setRole(role);
            return projectDeveloperRoleRepository.save(projectDeveloperRole);
        }
        return null; // You may want to throw an exception or return an error response
    }

    @Transactional
    public ProjectDeveloperRole updateRole(Integer projectId, Integer developerId, String newRole) {
        ProjectDeveloperId id = new ProjectDeveloperId(projectId, developerId);
        Optional<ProjectDeveloperRole> role = projectDeveloperRoleRepository.findById(id);
        if (role.isPresent()) {
            role.get().setRole(newRole);
            return projectDeveloperRoleRepository.save(role.get());
        }
        return null; // Handle as needed
    }

    @Transactional
    public void removeRole(Integer projectId, Integer developerId) {
        ProjectDeveloperId id = new ProjectDeveloperId(projectId, developerId);
        projectDeveloperRoleRepository.deleteById(id);
    }
}
