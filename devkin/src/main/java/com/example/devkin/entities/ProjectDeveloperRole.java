package com.example.devkin.entities;
import jakarta.persistence.*;

@Entity
@Table(name = "Project_Developers")
public class ProjectDeveloperRole {
    @EmbeddedId
    private ProjectDeveloperId id;

    @ManyToOne
    @MapsId("projectId")
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne
    @MapsId("developerId")
    @JoinColumn(name = "developer_id")
    private User developer;

    @Column(nullable = false)
    private String role;

    public ProjectDeveloperId getId() {
        return id;
    }

    public void setId(ProjectDeveloperId id) {
        this.id = id;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public User getDeveloper() {
        return developer;
    }

    public void setDeveloper(User developer) {
        this.developer = developer;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
