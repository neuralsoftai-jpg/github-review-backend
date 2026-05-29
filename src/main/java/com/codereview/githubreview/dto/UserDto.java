package com.codereview.githubreview.dto;

public class UserDto {
    private Long id;
    private String name;
    private String email;
    private String profileImage;
    private String role;

    // Manual Constructor
    public UserDto(Long id, String name, String email, String profileImage, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.profileImage = profileImage;
        this.role = role;
    }

    // ==========================================
    // MANUAL GETTERS & SETTERS
    // ==========================================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}