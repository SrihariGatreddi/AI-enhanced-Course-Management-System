package com.cms.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfig {

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private final WebClient webClient = WebClient.builder().build();

    // Mock AI recommendation for admin
    public String getCourseRecommendation(Long studentId) {
        // Replace with real AI logic
        return "Recommended Course: Advanced Java Programming";
    }

    public String getEmailDraft(Long studentId, String recommendation) {
        // Replace with real email draft logic
        return "Dear Student,\n\nBased on your profile, we recommend: " + recommendation + ".\nHappy Learning!\nCMS Team";
    }

    // Mock AI response for student chat
    public String getAiResponse(Long studentId, String question) {
        String prompt = "Provide a detailed response to the following question: '" + question + "'";

        try {
            String response = webClient.post()
                .uri(geminiApiUrl)
                .header("Authorization", "Bearer " + geminiApiKey)
                .bodyValue("{\"prompt\": \"" + prompt + "\"}")
                .retrieve()
                .bodyToMono(String.class)
                .block();

            return response != null ? response : "No response received from AI service.";
        } catch (Exception e) {
            return "Error communicating with AI service: " + e.getMessage();
        }
    }
}
