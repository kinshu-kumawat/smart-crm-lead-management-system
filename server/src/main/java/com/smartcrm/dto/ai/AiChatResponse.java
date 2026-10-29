package com.smartcrm.dto.ai;

import java.util.List;

public class AiChatResponse {
    private String reply;
    private List<String> suggestedQuestions;
    private String contextSummary;
    private String provider;

    public AiChatResponse() {}

    public AiChatResponse(String reply, List<String> suggestedQuestions, String contextSummary, String provider) {
        this.reply = reply;
        this.suggestedQuestions = suggestedQuestions;
        this.contextSummary = contextSummary;
        this.provider = provider;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public List<String> getSuggestedQuestions() {
        return suggestedQuestions;
    }

    public void setSuggestedQuestions(List<String> suggestedQuestions) {
        this.suggestedQuestions = suggestedQuestions;
    }

    public String getContextSummary() {
        return contextSummary;
    }

    public void setContextSummary(String contextSummary) {
        this.contextSummary = contextSummary;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }
}
