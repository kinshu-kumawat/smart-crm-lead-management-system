package com.smartcrm.dto.ai;

import java.time.LocalDateTime;

public class AiFollowUpResponse {
    private String subject;
    private String messageBody;
    private String channel;
    private String provider;
    private LocalDateTime generatedAt = LocalDateTime.now();

    public AiFollowUpResponse() {}

    public AiFollowUpResponse(String subject, String messageBody, String channel, String provider) {
        this.subject = subject;
        this.messageBody = messageBody;
        this.channel = channel;
        this.provider = provider;
        this.generatedAt = LocalDateTime.now();
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }
}
