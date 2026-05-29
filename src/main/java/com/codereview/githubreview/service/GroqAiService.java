package com.codereview.githubreview.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException; // YEH IMPORT ZAROORI HAI

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GroqAiService {

    private final WebClient groqWebClient;

    @Value("${groq.model.name}")
    private String groqModel;

    public GroqAiService(WebClient groqWebClient) {
        this.groqWebClient = groqWebClient;
    }

    public String getCodeReview(String codeDiff) {
        System.out.println("Sending diff to Groq AI for review...");

        String systemPrompt = "You are a strict senior developer. Review this git diff. " +
                "Format your response in Markdown with these EXACT headings:\n" +
                "### 🐛 Issues Found\n" +
                "(List the bugs, bad practices, or vulnerabilities directly. No fluff.)\n" +
                "### 🛠️ Fixed Code\n" +
                "(Provide the exact corrected code inside markdown code blocks so it can be copied.)\n" +
                "If the code is perfect, just return 'No critical issues found.'";

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", groqModel);
        requestBody.put("messages", List.of(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user", "content", "Review this git diff:\n" + codeDiff)
        ));

        try {
            // Groq API call
            return groqWebClient.post()
                    .uri("/chat/completions")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

        } catch (WebClientResponseException e) {
            // THE BRUTAL TRUTH EXTRACTOR: Yeh Groq ka actual error print karega
            System.err.println("Groq API EXACT Error: " + e.getResponseBodyAsString());
            return "Error: API rejected the request.";
        } catch (Exception e) {
            System.err.println("System failed: " + e.getMessage());
            return "Error generating code review.";
        }
    }
}