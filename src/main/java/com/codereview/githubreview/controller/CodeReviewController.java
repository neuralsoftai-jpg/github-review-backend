package com.codereview.githubreview.controller;

import com.codereview.githubreview.entity.CodeReview;
import com.codereview.githubreview.repository.CodeReviewRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
public class CodeReviewController {

    private final CodeReviewRepository repository;

    public CodeReviewController(CodeReviewRepository repository) {
        this.repository = repository;
    }

    // 1. Get ONLY the Logged-in User's Reviews
    @GetMapping
    public ResponseEntity<List<CodeReview>> getAllReviews(Authentication authentication) {
        // JWT Filter automatically authentication.getName() mein user ka email set kar deta hai
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).build();
        }
        String email = authentication.getName();

        List<CodeReview> userReviews = repository.findByUserEmailOrderByIdDesc(email);
        return ResponseEntity.ok(userReviews);
    }

    // 2. Secure Single Review Fetch (Anti-IDOR)
    @GetMapping("/{id}")
    public ResponseEntity<CodeReview> getReviewById(@PathVariable Long id, Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).build();
        }
        String email = authentication.getName();

        return repository.findByIdAndUserEmail(id, email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 3. Get User-Specific Dashboard Stats
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).build();
        }
        String email = authentication.getName();

        Map<String, Object> stats = new HashMap<>();
        long totalReviews = repository.countByUserEmail(email);

        stats.put("totalReviews", totalReviews);
        stats.put("status", "Active");
        return ResponseEntity.ok(stats);
    }
}