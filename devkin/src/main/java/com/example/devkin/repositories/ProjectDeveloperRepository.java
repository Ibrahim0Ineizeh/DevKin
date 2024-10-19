package com.example.devkin.repositories;

import com.example.devkin.entities.Project;
import com.example.devkin.entities.ProjectDeveloperId;
import com.example.devkin.entities.ProjectDeveloperRole;
import com.example.devkin.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectDeveloperRepository extends JpaRepository<ProjectDeveloperRole, ProjectDeveloperId> {
    // Custom query to find developers by project
    List<ProjectDeveloperRole> findByProject(Project project);

    // Custom query to find projects by developer
    List<ProjectDeveloperRole> findByDeveloper(User developer);
}
