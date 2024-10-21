package com.example.devkin.repositories;

import com.example.devkin.entities.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {
    Optional<File> findByFilePath(String filePath);
    void deleteByFilePath(String filePath);
}