package com.example.devkin.repositories;
import com.example.devkin.entities.ProjectDeveloperRole;
import com.example.devkin.entities.ProjectDeveloperId;
import com.example.devkin.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectDeveloperRoleRepository extends JpaRepository<ProjectDeveloperRole, ProjectDeveloperId> {
    List<ProjectDeveloperRole> findByProject_ProjectId(Integer projectId);
    ProjectDeveloperRole findByDeveloper_Id(Integer developerId);
    ProjectDeveloperRole findByProject_ProjectIdAndDeveloper_Id(Integer projectId, Integer developerId);
    List<ProjectDeveloperRole> findByProject_ProjectIdAndRole(Integer projectId, String role);
    Optional<ProjectDeveloperRole> findById(ProjectDeveloperId id);


    @Query("SELECT pd.developer FROM ProjectDeveloperRole pd " +
            "WHERE pd.project.id = :projectId AND pd.developer.id <> pd.project.owner.id")
    List<User> findAllUsersByProjectIdExceptOwner(@Param("projectId") Integer projectId);
}
