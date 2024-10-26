package com.example.devkin.repositories;

import com.example.devkin.entities.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.id NOT IN " +
            "(SELECT d.developer.id FROM ProjectDeveloperRole d WHERE d.project.id = :projectId)")
    List<User> findUsersNotInProject(@Param("projectId") Integer projectId);
}
