package com.codereview.githubreview.controller;

import com.codereview.githubreview.entity.User;
import com.codereview.githubreview.repository.UserRepository;
import com.codereview.githubreview.service.WebhookProcessingService;
import com.codereview.githubreview.util.HmacUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/webhook")
public class WebhookController {

    private final WebhookProcessingService processingService;
    private final HmacUtils hmacUtils;
    private final UserRepository userRepository; // User Repository add ki

    public WebhookController(WebhookProcessingService processingService, HmacUtils hmacUtils, UserRepository userRepository) {
        this.processingService = processingService;
        this.hmacUtils = hmacUtils;
        this.userRepository = userRepository;
    }

    // 🔥 URL ab dynamic path variable {webhookId} lega
    @PostMapping("/github/{webhookId}")
    public ResponseEntity<String> handleGitHubWebhook(
            @PathVariable String webhookId,
            @RequestHeader(value = "X-GitHub-Event", required = false) String eventType,
            @RequestHeader(value = "X-Hub-Signature-256", required = false) String signature,
            @RequestBody String payload) {

        if (!"push".equals(eventType)) {
            return ResponseEntity.ok("Ignored: Not a push event");
        }

        if (signature == null || signature.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing signature");
        }

        // 1. Dhoondho yeh Webhook kis user ka hai
        Optional<User> userOpt = userRepository.findByWebhookId(webhookId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid Webhook URL");
        }
        User owner = userOpt.get();

        // 2. Security Check: Us specific user ke secret se signature verify karo
        String expectedSignature = hmacUtils.calculateHmac256(payload, owner.getWebhookSecret());
        if (!expectedSignature.equals(signature)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid HMAC signature");
        }

        // 3. Processing layer ko payload AND owner ki true email pass karo
        processingService.processPushEvent(payload, owner.getEmail());

        return ResponseEntity.ok("Webhook processed successfully");
    }
}