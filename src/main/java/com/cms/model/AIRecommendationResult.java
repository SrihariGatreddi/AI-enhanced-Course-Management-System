package com.cms.model;

public class
AIRecommendationResult {
    private String recommendation;
    private String emailDraft;

    public AIRecommendationResult(String recommendation, String emailDraft) {
        this.recommendation = recommendation;
        this.emailDraft = emailDraft;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public String getEmailDraft() {
        return emailDraft;
    }

    public void setEmailDraft(String emailDraft) {
        this.emailDraft = emailDraft;
    }
}

