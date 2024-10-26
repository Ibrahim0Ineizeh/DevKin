package com.example.devkin.dtos;

import java.time.LocalDateTime;

public class FileStructureDto {
    private Integer fileId;
    private String fileName;
    private String filePath;
    private String fileType;
    private long fileSize;
    private LocalDateTime lastModefied;

    public Integer getFileId() {
        return fileId;
    }

    public void setFileId(Integer fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public LocalDateTime getLastModefied() {
        return lastModefied;
    }

    public void setLastModefied(LocalDateTime lastModefied) {
        this.lastModefied = lastModefied;
    }
}
