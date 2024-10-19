package com.example.devkin.services;

import com.example.devkin.entities.File;
import com.example.devkin.entities.Folder;
import com.example.devkin.entities.Project;
import com.example.devkin.repositories.FileRepository;
import com.example.devkin.repositories.FolderRepository;
import com.example.devkin.repositories.ProjectRepository;
import io.minio.*;
import io.minio.errors.MinioException;
import io.minio.messages.Item;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class FileStorageService {

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private FolderRepository folderRepository;

    @Transactional
    public void createFolder(String folderPath, String folderName, Integer projectId) {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket("devkin")
                            .object(folderPath.endsWith("/") ? folderPath : folderPath + "/")
                            .stream(new ByteArrayInputStream(new byte[0]), 0, 0)
                            .contentType("application/octet-stream")
                            .build()
            );

            // Save folder in the database
            Folder folder = new Folder();
            folder.setFolderName(folderName);
            folder.setFolderPath(folderPath);
            folder.setCreatedAt(LocalDateTime.now());
            folder.setLastModified(LocalDateTime.now());
            folder.setProject(projectRepository.findById(projectId).orElseThrow(() -> new RuntimeException("Project not found")));

            folderRepository.save(folder);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create folder", e);
        }
    }

    @Transactional
    public void copyFolder(String oldFolderPath, String newFolderPath) {
        try {
            Iterable<Result<Item>> items = minioClient.listObjects(ListObjectsArgs.builder()
                    .bucket("devkin")
                    .prefix(oldFolderPath)
                    .recursive(true)
                    .build());

            for (Result<Item> result : items) {
                Item item = result.get();
                String newObjectPath = newFolderPath + item.objectName().substring(oldFolderPath.length());

                minioClient.copyObject(
                        CopyObjectArgs.builder()
                                .bucket("devkin")
                                .source(CopySource.builder().bucket("devkin").object(item.objectName()).build())
                                .object(newObjectPath)
                                .build()
                );
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to copy folder", e);
        }
    }

    @Transactional
    public void updateFolder(String oldFolderPath, String newFolderName) {
        try {
            // Copy the folder in Minio
            copyFolder(oldFolderPath, newFolderName);

            // Update folder name in the database
            Folder folder = folderRepository.findByFolderPath(oldFolderPath)
                    .orElseThrow(() -> new RuntimeException("Folder not found"));

            String newFolderPath = folder.getFolderPath().replace(oldFolderPath, newFolderName);
            folder.setFolderName(newFolderName);
            folder.setFolderPath(newFolderPath);
            folder.setLastModified(LocalDateTime.now());

            folderRepository.save(folder);

            // Delete old folder after updating
            deleteFolder(oldFolderPath);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update folder name", e);
        }
    }

    @Transactional
    public void deleteFolder(String folderPath) {
        try {
            // List all objects in the folder
            Iterable<Result<Item>> items = minioClient.listObjects(ListObjectsArgs.builder()
                    .bucket("devkin")
                    .prefix(folderPath) // Prefix for the folder
                    .recursive(true)    // Recursively delete contents
                    .build());

            // Delete each object
            for (Result<Item> result : items) {
                Item item = result.get();
                minioClient.removeObject(RemoveObjectArgs.builder()
                        .bucket("devkin")
                        .object(item.objectName())
                        .build());
            }

            // Optionally delete folder metadata from your database
            folderRepository.deleteByFolderPath(folderPath);

        } catch (Exception e) {
            throw new RuntimeException("Failed to delete folder", e);
        }
    }

    @Transactional
    public void createFileInFolder(String folderPath, String fileName, byte[] fileData, String contentType, long fileSize, Integer projectId) {
        try {
            String filePath = folderPath.endsWith("/") ? folderPath + fileName : folderPath + "/" + fileName;

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket("devkin")
                            .object(filePath)
                            .stream(new ByteArrayInputStream(fileData), fileData.length, -1)
                            .contentType(contentType)
                            .build()
            );

            // Save file in the database
            File file = new File();
            file.setFileName(fileName);
            file.setFilePath(filePath);
            file.setFileType(contentType);
            file.setFileSize(fileSize);
            file.setCreatedAt(LocalDateTime.now());
            file.setLastModified(LocalDateTime.now());
            file.setLastModifiedBy(authenticationService.authenticatedUser().getId());
            file.setProject(projectRepository.findById(projectId)
                    .orElseThrow(() -> new RuntimeException("Project not found")));

            fileRepository.save(file);
            Optional<Project> p = projectRepository.findById(projectId);
            p.get().setSize(p.get().getSize() + fileSize);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create file", e);
        }
    }

    @Transactional
    public void updateFileName(String oldFilePath, String newFileName) {
        try {
            String newFilePath = oldFilePath.substring(0, oldFilePath.lastIndexOf("/") + 1) + newFileName;

            minioClient.copyObject(
                    CopyObjectArgs.builder()
                            .bucket("devkin")
                            .source(CopySource.builder().bucket("devkin").object(oldFilePath).build())
                            .object(newFilePath)
                            .build()
            );

            File file = fileRepository.findByFilePath(oldFilePath)
                    .orElseThrow(() -> new RuntimeException("File not found"));
            file.setFileName(newFileName);
            file.setFilePath(newFilePath);
            file.setLastModified(LocalDateTime.now());
            file.setLastModifiedBy(authenticationService.authenticatedUser().getId());
            deleteFile(oldFilePath);
            fileRepository.save(file);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update file name", e);
        }
    }

    @Transactional
    public void updateFileContents(String filePath, byte[] newFileData, String newContentType) {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket("devkin")
                            .object(filePath)
                            .stream(new ByteArrayInputStream(newFileData), newFileData.length, -1)
                            .contentType(newContentType)
                            .build()
            );

            File file = fileRepository.findByFilePath(filePath)
                    .orElseThrow(() -> new RuntimeException("File not found"));
            file.setFileType(newContentType);
            file.setLastModified(LocalDateTime.now());
            file.setLastModifiedBy(authenticationService.authenticatedUser().getId());

            fileRepository.save(file);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update file contents", e);
        }
    }

    @Transactional
    public void deleteFile(String filePath) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket("devkin")
                            .object(filePath)
                            .build()
            );

            // Remove file from the database
            fileRepository.deleteByFilePath(filePath);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete file", e);
        }
    }
}
