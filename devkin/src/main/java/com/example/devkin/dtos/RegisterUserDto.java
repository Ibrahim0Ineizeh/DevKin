package com.example.devkin.dtos;

public class RegisterUserDto {
    private String email;

    private String password;

    private String username;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String fullName) {
        this.username = fullName;
    }

}
