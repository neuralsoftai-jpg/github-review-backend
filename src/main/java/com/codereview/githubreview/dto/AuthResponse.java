package com.codereview.githubreview.dto;

public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private UserDto user;

    // Manual Constructor
    public AuthResponse(String accessToken, String refreshToken, UserDto user) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.user = user;
    }

    // --- Manual Getters & Setters ---
    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

    public UserDto getUser() { return user; }
    public void setUser(UserDto user) { this.user = user; }
}