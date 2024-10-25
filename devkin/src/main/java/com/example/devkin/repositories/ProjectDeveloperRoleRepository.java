package com.example.devkin.repositories;
import com.example.devkin.entities.ProjectDeveloperRole;
import com.example.devkin.entities.ProjectDeveloperId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectDeveloperRoleRepository extends JpaRepository<ProjectDeveloperRole, ProjectDeveloperId> {
    List<ProjectDeveloperRole> findByProject_ProjectId(Integer projectId);
    List<ProjectDeveloperRole> findByDeveloper_Id(Integer developerId);
    ProjectDeveloperRole findByProject_ProjectIdAndDeveloper_Id(Integer projectId, Integer developerId);
    List<ProjectDeveloperRole> findByProject_ProjectIdAndRole(Integer projectId, String role);
}
