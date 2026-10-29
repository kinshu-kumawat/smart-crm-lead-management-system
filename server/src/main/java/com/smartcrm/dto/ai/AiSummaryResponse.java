package com.smartcrm.dto.ai;

import java.util.List;

public class AiSummaryResponse {
    private Long leadId;
    private String summary;
    private List<String> keyHighlights;
    private List<String> recommendedNextSteps;
    private String provider;

    public AiSummaryResponse() {}

    public AiSummaryResponse(Long leadId, String summary, List<String> keyHighlights, List<String> recommendedNextSteps, String provider) {
        this.leadId = leadId;
        this.summary = summary;
        this.keyHighlights = keyHighlights;
        this.recommendedNextSteps = recommendedNextSteps;
        this.provider = provider;
    }

    public Long getLeadId() {
        return leadId;
    }

    public void setLeadId(Long leadId) {
        this.leadId = leadId;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<String> getKeyHighlights() {
        return keyHighlights;
    }

    public void setKeyHighlights(List<String> keyHighlights) {
        this.keyHighlights = keyHighlights;
    }

    public List<String> getRecommendedNextSteps() {
        return recommendedNextSteps;
    }

    public void setRecommendedNextSteps(List<String> recommendedNextSteps) {
        this.recommendedNextSteps = recommendedNextSteps;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }
}
