package com.smartcrm.dto.ai;

import com.smartcrm.entity.enums.Priority;

public class AiScoreResponse {
    private Long leadId;
    private int score;
    private Priority priorityRecommendation;
    private String explanation;
    private String suggestedNextAction;
    private String provider;

    public AiScoreResponse() {}

    public AiScoreResponse(Long leadId, int score, Priority priorityRecommendation, String explanation, String suggestedNextAction, String provider) {
        this.leadId = leadId;
        this.score = score;
        this.priorityRecommendation = priorityRecommendation;
        this.explanation = explanation;
        this.suggestedNextAction = suggestedNextAction;
        this.provider = provider;
    }

    public Long getLeadId() {
        return leadId;
    }

    public void setLeadId(Long leadId) {
        this.leadId = leadId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Priority getPriorityRecommendation() {
        return priorityRecommendation;
    }

    public void setPriorityRecommendation(Priority priorityRecommendation) {
        this.priorityRecommendation = priorityRecommendation;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getSuggestedNextAction() {
        return suggestedNextAction;
    }

    public void setSuggestedNextAction(String suggestedNextAction) {
        this.suggestedNextAction = suggestedNextAction;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }
}
