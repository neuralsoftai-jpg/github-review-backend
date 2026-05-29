package com.codereview.githubreview.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class GitHubApiService {

    private final WebClient webClient;

    public GitHubApiService(WebClient githubWebClient) {
        this.webClient = githubWebClient;
    }

    public String fetchCommitDiff(String repoFullName, String commitId) {
        // Fix: "owner/repo" ko split karke alag variables banayenge
        // Taaki Spring '/' ko '%2F' mein encode na kare
        String[] repoParts = repoFullName.split("/");
        String owner = repoParts[0];
        String repoName = repoParts[1];

        System.out.println("Fetching diff for -> Owner: " + owner + ", Repo: " + repoName + ", Commit: " + commitId);

        return webClient.get()
                // Ab URI mein owner aur repoName alag se pass ho rahe hain
                .uri("/repos/{owner}/{repo}/commits/{commitId}", owner, repoName, commitId)
                .header("Accept", "application/vnd.github.v3.diff")
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}