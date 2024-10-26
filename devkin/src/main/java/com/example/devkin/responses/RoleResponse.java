package com.example.devkin.responses;

public class RoleResponse {
    private String name;
    private String email;
    private String role;

    public RoleResponse() {
    }

    public RoleResponse(String name, String email, String role) {
        this.name = name;
        this.email = email;
        this.role = role;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
