package com.codereview.githubreview.service;

import com.codereview.githubreview.entity.CodeReview;
import com.codereview.githubreview.repository.CodeReviewRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class WebhookProcessingService {

    private final ObjectMapper objectMapper;
    private final GitHubApiService gitHubApiService;
    private final GroqAiService groqAiService;
    private final CodeReviewRepository codeReviewRepository;

    public WebhookProcessingService(ObjectMapper objectMapper,
                                    GitHubApiService gitHubApiService,
                                    GroqAiService groqAiService,
                                    CodeReviewRepository codeReviewRepository) {
        this.objectMapper = objectMapper;
        this.gitHubApiService = gitHubApiService;
        this.groqAiService = groqAiService;
        this.codeReviewRepository = codeReviewRepository;
    }

    // 🔥 FIX: Naya parameter 'ownerEmail' add kiya hai jo seedha database lookup se aayega
    public void processPushEvent(String payload, String ownerEmail) {
        try {
            JsonNode rootNode = objectMapper.readTree(payload);

            String repoFullName = rootNode.path("repository").path("full_name").asText();
            String headCommitId = rootNode.path("head_commit").path("id").asText();

            if (headCommitId.isEmpty() || repoFullName.isEmpty()) {
                System.out.println("Processing skipped: Critical data (Commit ID or Repo Name) missing.");
                return;
            }

            System.out.println("Owner Email (DB): " + ownerEmail);
            System.out.println("Repository: " + repoFullName);
            System.out.println("Commit SHA: " + headCommitId);

            // 1. Fetch Diff (Original GitHub Code)
            String codeDiff = gitHubApiService.fetchCommitDiff(repoFullName, headCommitId);
            if (codeDiff == null || codeDiff.isEmpty()) {
                System.out.println("No code changes found.");
                return;
            }

            // 2. Fetch AI Review
            String rawAiResponse = groqAiService.getCodeReview(codeDiff);

            // 3. Extract actual markdown text
            String cleanReviewText = rawAiResponse;
            try {
                JsonNode groqNode = objectMapper.readTree(rawAiResponse);
                if (groqNode.has("choices")) {
                    cleanReviewText = groqNode.path("choices").get(0).path("message").path("content").asText();
                }
            } catch (Exception e) {
                System.out.println("Groq JSON parsing failed. Using raw response.");
            }

            // 4. Save to Database using the 100% reliable ownerEmail
            CodeReview review = new CodeReview(
                    ownerEmail,
                    repoFullName,
                    headCommitId,
                    cleanReviewText,
                    codeDiff,
                    LocalDateTime.now()
            );

            codeReviewRepository.save(review);

            System.out.println("✅ Successfully saved AI Review & Original Code! ID: " + review.getId() + " For User: " + ownerEmail);

        } catch (Exception e) {
            System.err.println("Failed to process webhook payload: " + e.getMessage());
        }
    }
}