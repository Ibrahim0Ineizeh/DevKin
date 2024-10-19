package com.example.devkin.repositories;

import com.example.devkin.entities.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

import java.util.List;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Long> {
    Optional<Folder> findByFolderPath(String folderPath);
    void deleteByFolderPath(String folderPath);
}
