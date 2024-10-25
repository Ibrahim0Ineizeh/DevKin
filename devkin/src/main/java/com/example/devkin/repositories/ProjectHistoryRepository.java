package com.example.devkin.repositories;

import com.example.devkin.entities.Project;
import com.example.devkin.entities.ProjectHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectHistoryRepository extends JpaRepository<ProjectHistory, Integer> {
    // Custom query to find history by project
    List<ProjectHistory> findByProject(Project project);
}
