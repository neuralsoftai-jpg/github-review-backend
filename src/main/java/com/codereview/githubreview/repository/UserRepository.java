package com.codereview.githubreview.repository;

import com.codereview.githubreview.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    // 🔥 NAYA METHOD: Webhook aate hi user ko URL id se dhoondhne ke liye
    Optional<User> findByWebhookId(String webhookId);
}