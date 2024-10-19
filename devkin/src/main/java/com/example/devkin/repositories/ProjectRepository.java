package com.example.devkin.repositories;

import com.example.devkin.entities.Project;
import com.example.devkin.entities.User;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer> {
    Project findByName(String name);
    boolean existsByOwnerAndName(User owner, String name);
}
