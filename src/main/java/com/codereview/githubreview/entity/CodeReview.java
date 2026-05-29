package com.codereview.githubreview.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "code_reviews")
public class CodeReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔥 NAYA FIELD: Data Isolation ke liye (Yeh batayega ki review kiska hai)
    @Column(name = "user_email")
    private String userEmail;

    private String repoName;
    private String commitSha;

    // JSON annotation ensure karega ki frontend ko exact 'aiReview' naam mile
    @Column(columnDefinition = "TEXT")
    @JsonProperty("aiReview")
    private String aiReview;

    // Original Code ke liye
    @Column(columnDefinition = "TEXT")
    @JsonProperty("originalCode")
    private String originalCode;

    private LocalDateTime createdAt;

    // Default Constructor (Hibernate ke liye zaroori)
    public CodeReview() {}

    // Updated Constructor (Isme userEmail add kiya gaya hai)
    public CodeReview(String userEmail, String repoName, String commitSha, String aiReview, String originalCode, LocalDateTime createdAt) {
        this.userEmail = userEmail;
        this.repoName = repoName;
        this.commitSha = commitSha;
        this.aiReview = aiReview;
        this.originalCode = originalCode;
        this.createdAt = createdAt;
    }

    // --- Getters & Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getRepoName() { return repoName; }
    public void setRepoName(String repoName) { this.repoName = repoName; }

    public String getCommitSha() { return commitSha; }
    public void setCommitSha(String commitSha) { this.commitSha = commitSha; }

    public String getAiReview() { return aiReview; }
    public void setAiReview(String aiReview) { this.aiReview = aiReview; }

    public String getOriginalCode() { return originalCode; }
    public void setOriginalCode(String originalCode) { this.originalCode = originalCode; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}