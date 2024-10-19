package com.example.devkin.repositories;

import com.example.devkin.entities.Project;
import com.example.devkin.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer> {
    Project findByName(String name);
    boolean existsBySlug(String slug);
    boolean existsByOwnerAndName(User owner, String name);
    Optional<Project> findBySlug(String slug);
}
