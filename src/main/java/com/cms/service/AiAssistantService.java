package com.cms.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Collections;
import java.util.List;

@Service
public class AiAssistantService {

    private  WebClient webClient;
    private  String apiKey;
    private  String apiUrl;

    public AiAssistantService(@Value("${gemini.api.key}") String apiKey,
                              @Value("${gemini.api.url}") String apiUrl) {
        this.apiKey = apiKey;
        this.apiUrl = apiUrl;
        this.webClient = WebClient.builder().build();
    }

    public String getAiResponse(Long studentId, String question) {
        String prompt = "As an expert academic assistant, provide a clear and helpful response to the following student question: '" + question + "'";
        return callGeminiApi(prompt);
    }

    // Unchanged methods...
    public String getCourseRecommendation(String courseHistory) {
        return "Recommended courses based on: " + courseHistory;
    }

    public String getEmailDraft(String studentName, String recommendation) {
        return "Dear " + studentName + ", we recommend: " + recommendation;
    }

    public static class AiLimitExceededException extends RuntimeException {
        public AiLimitExceededException(String message) {
            super(message);
        }
    }

    private String callGeminiApi(String prompt) {
        String urlWithKey = this.apiUrl + "?key=" + this.apiKey;

        GeminiRequest requestBody = new GeminiRequest(
                Collections.singletonList(new GeminiRequest.Content(
                        Collections.singletonList(new GeminiRequest.Part(prompt))
                ))
        );

        try {
            GeminiResponse geminiResponse = webClient.post()
                    .uri(urlWithKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(GeminiResponse.class)
                    .block();

            if (geminiResponse != null && geminiResponse.candidates() != null && !geminiResponse.candidates().isEmpty()) {
                if ("SAFETY".equals(geminiResponse.candidates().get(0).finishReason())) {
                    return "I cannot answer that question as it violates safety guidelines.";
                }
                return geminiResponse.candidates().get(0).content().parts().get(0).text();
            }

            return "The AI returned an empty response. Please try rephrasing your question.";

        } catch (WebClientResponseException e) {
            // --- CATCHES SPECIFIC HTTP ERRORS (LIKE 429, 400, 500) ---
            System.err.println("Gemini API HTTP Error: Status " + e.getStatusCode() + ", Body: " + e.getResponseBodyAsString());

            // **Directly and reliably checks the HTTP status code**
            if (e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                throw new AiLimitExceededException("AI Assistant Limit Exceeded: The free API call limit has been reached. Please try again later.");
            }

            // For any other HTTP error, return the generic message
            return "Sorry, the AI assistant is currently unavailable due to a technical issue.";

        } catch (Exception e) {
            // --- CATCHES OTHER ERRORS (LIKE NETWORK TIMEOUTS) ---
            System.err.println("Gemini API General Error: " + e.getMessage());
            return "Sorry, the AI assistant is currently unavailable due to a technical issue.";
        }
    }

    // --- DTOs (Data Transfer Objects) for clean JSON mapping ---
    private record GeminiRequest(List<Content> contents) {
        private record Content(List<Part> parts) {}
        private record Part(String text) {}
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record GeminiResponse(List<Candidate> candidates) {
        private record Candidate(Content content, String finishReason) {}
        private record Content(List<Part> parts) {}
        private record Part(String text) {}
    }
}