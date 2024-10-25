package com.example.devkin.repositories;

import com.example.devkin.entities.Project;
import com.example.devkin.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer> {
    Project findByName(String name);
    boolean existsBySlug(String slug);
    boolean existsByOwnerAndName(User owner, String name);
    Optional<Project> findBySlug(String slug);

    @Query("SELECT p FROM Project p WHERE p.owner = :user OR :user MEMBER OF p.developers")
    List<Project> findByOwnerOrDeveloper(@Param("user") User user);
}
