package com.example.devkin.entities;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "Project")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Integer projectId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private long size = 0;

    @Column(nullable = false)
    private String url;

    @Column(nullable = true)
    private String description;

    @Column(nullable = false)
    private String language;

    @Column(unique = true)
    private String slug;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(name = "last_modified", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime lastModified;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @ManyToMany
    @JoinTable(
            name = "Project_Developers",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "developer_id")
    )
    private Set<User> developers;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private Set<ProjectHistory> projectHistories;

    @OneToMany(mappedBy = "project", cascade = CascadeType.REMOVE)
    private Set<File> files;

    @OneToMany(mappedBy = "project", cascade = CascadeType.REMOVE)
    private Set<Folder> folder;

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Set<User> getDevelopers() {
        return developers;
    }

    public void setDevelopers(Set<User> developers) {
        this.developers = developers;
    }

    public Set<ProjectHistory> getProjectHistories() {
        return projectHistories;
    }

    public void setProjectHistories(Set<ProjectHistory> projectHistories) {
        this.projectHistories = projectHistories;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public Set<File> getFiles() {
        return files;
    }

    public void setFiles(Set<File> files) {
        this.files = files;
    }

    public Set<Folder> getFolder() {
        return folder;
    }

    public void setFolder(Set<Folder> folder) {
        this.folder = folder;
    }
}
