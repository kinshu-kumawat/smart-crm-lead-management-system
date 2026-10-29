package com.smartcrm.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartcrm.dto.ai.*;
import com.smartcrm.entity.Activity;
import com.smartcrm.entity.FollowUp;
import com.smartcrm.entity.Lead;
import com.smartcrm.entity.User;
import com.smartcrm.entity.enums.FollowUpStatus;
import com.smartcrm.entity.enums.LeadStatus;
import com.smartcrm.entity.enums.Priority;
import com.smartcrm.repository.ActivityRepository;
import com.smartcrm.repository.FollowUpRepository;
import com.smartcrm.repository.LeadRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class AiService {

    private static final Logger log = LoggerFactory.getLogger(AiService.class);

    private final LeadRepository leadRepository;
    private final ActivityRepository activityRepository;
    private final FollowUpRepository followUpRepository;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    @Value("${smartcrm.ai.gemini-api-key:}")
    private String geminiApiKey;

    @Value("${smartcrm.ai.model:gemini-1.5-flash}")
    private String geminiModel;

    @Value("${smartcrm.ai.timeout-ms:10000}")
    private int timeoutMs;

    public AiService(LeadRepository leadRepository,
                     ActivityRepository activityRepository,
                     FollowUpRepository followUpRepository,
                     ObjectMapper objectMapper,
                     RestTemplateBuilder restTemplateBuilder) {
        this.leadRepository = leadRepository;
        this.activityRepository = activityRepository;
        this.followUpRepository = followUpRepository;
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofMillis(8000))
                .setReadTimeout(Duration.ofMillis(12000))
                .build();
    }

    public boolean isGeminiConfigured() {
        return geminiApiKey != null && !geminiApiKey.trim().isEmpty() && !geminiApiKey.contains("YOUR_");
    }

    @Transactional
    public AiScoreResponse scoreLead(Long leadId, User currentUser) {
        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new RuntimeException("Lead not found: " + leadId));

        List<Activity> activities = activityRepository.findByLeadIdOrderByActivityTimeDesc(leadId);

        if (isGeminiConfigured()) {
            try {
                String prompt = buildLeadScoringPrompt(lead, activities);
                String geminiResponse = callGeminiApi(prompt);
                AiScoreResponse result = parseGeminiScoreResponse(geminiResponse, leadId);

                // Update lead entity
                lead.setAiScore(result.getScore());
                lead.setAiScoreExplanation(result.getExplanation());
                lead.setAiRecommendedAction(result.getSuggestedNextAction());
                lead.setAiLastScoredAt(LocalDateTime.now());
                leadRepository.save(lead);

                return result;
            } catch (Exception e) {
                log.warn("Gemini API call failed or timed out. Falling back to heuristic engine. Error: {}", e.getMessage());
            }
        }

        // Fallback intelligent heuristic engine
        AiScoreResponse fallback = calculateHeuristicScore(lead, activities);
        lead.setAiScore(fallback.getScore());
        lead.setAiScoreExplanation(fallback.getExplanation());
        lead.setAiRecommendedAction(fallback.getSuggestedNextAction());
        lead.setAiLastScoredAt(LocalDateTime.now());
        leadRepository.save(lead);

        return fallback;
    }

    public AiFollowUpResponse generateFollowUpMessage(AiFollowUpRequest request, User currentUser) {
        Lead lead = leadRepository.findById(request.getLeadId())
                .orElseThrow(() -> new RuntimeException("Lead not found: " + request.getLeadId()));

        List<Activity> activities = activityRepository.findByLeadIdOrderByActivityTimeDesc(request.getLeadId());

        if (isGeminiConfigured()) {
            try {
                String prompt = buildFollowUpPrompt(lead, activities, request);
                String geminiResponse = callGeminiApi(prompt);
                return parseGeminiFollowUpResponse(geminiResponse, request.getChannel());
            } catch (Exception e) {
                log.warn("Gemini follow-up generation failed: {}. Falling back to template engine.", e.getMessage());
            }
        }

        return generateFallbackFollowUpMessage(lead, activities, request);
    }

    public AiSummaryResponse generateLeadSummary(Long leadId, User currentUser) {
        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new RuntimeException("Lead not found: " + leadId));

        List<Activity> activities = activityRepository.findByLeadIdOrderByActivityTimeDesc(leadId);

        if (isGeminiConfigured()) {
            try {
                String prompt = buildLeadSummaryPrompt(lead, activities);
                String geminiResponse = callGeminiApi(prompt);
                return parseGeminiSummaryResponse(geminiResponse, leadId);
            } catch (Exception e) {
                log.warn("Gemini lead summary failed: {}. Falling back to heuristic summary.", e.getMessage());
            }
        }

        return generateFallbackLeadSummary(lead, activities);
    }

    public AiChatResponse handleChatQuery(AiChatRequest request, User currentUser) {
        String query = request.getMessage().toLowerCase();

        // 1. Gather real CRM context based on query
        StringBuilder contextBuilder = new StringBuilder();
        List<String> suggestedQuestions = Arrays.asList(
                "Which leads need follow-up today?",
                "What is our current pipeline total value?",
                "Which high-priority leads are in negotiation?",
                "Summarize recently added leads"
        );

        if (request.getLeadId() != null) {
            Lead lead = leadRepository.findById(request.getLeadId()).orElse(null);
            if (lead != null) {
                contextBuilder.append(String.format("Focused Lead: %s (%s), Title: %s, Deal: $%s, Status: %s, Score: %s. ",
                        lead.getFullName(), lead.getCompany(), lead.getJobTitle(), lead.getEstimatedDealValue(),
                        lead.getStatus(), lead.getAiScore() != null ? lead.getAiScore() : "Unscored"));
            }
        }

        // Include today's followups summary
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);
        long todayFollowUps = followUpRepository.countDueToday(FollowUpStatus.PENDING, startOfDay, endOfDay);
        long overdueFollowUps = followUpRepository.countOverdue(FollowUpStatus.PENDING, LocalDateTime.now());
        BigDecimal totalPipeline = leadRepository.sumTotalEstimatedDealValue();
        long totalLeads = leadRepository.count();

        contextBuilder.append(String.format("CRM Metrics: %d total leads, $%s total pipeline value, %d follow-ups due today, %d overdue follow-ups.",
                totalLeads, totalPipeline != null ? totalPipeline : "0.00", todayFollowUps, overdueFollowUps));

        if (isGeminiConfigured()) {
            try {
                String prompt = String.format("You are an intelligent, proactive AI Sales Assistant inside a CRM system. " +
                        "CRM Context: %s\n" +
                        "User Question: \"%s\"\n" +
                        "Provide a concise, helpful, actionable response for the sales team. Keep your response within 2-3 short paragraphs.",
                        contextBuilder, request.getMessage());

                String reply = callGeminiApi(prompt);
                return new AiChatResponse(cleanAiMarkdown(reply), suggestedQuestions, contextBuilder.toString(), "Google Gemini (" + geminiModel + ")");
            } catch (Exception e) {
                log.warn("Gemini chat failed: {}. Using structured rule-based assistant.", e.getMessage());
            }
        }

        // Fallback intelligent conversational handler
        String reply = buildSmartChatReply(query, request.getLeadId(), todayFollowUps, overdueFollowUps, totalPipeline, totalLeads);
        return new AiChatResponse(reply, suggestedQuestions, contextBuilder.toString(), "Smart CRM Assistant (Offline / Heuristic)");
    }

    private String buildSmartChatReply(String query, Long leadId, long todayFollowUps, long overdueFollowUps, BigDecimal totalPipeline, long totalLeads) {
        if (query.contains("follow-up") || query.contains("today") || query.contains("due")) {
            return String.format("You currently have **%d follow-up task(s) due today** and **%d overdue task(s)** requiring immediate attention. " +
                    "I recommend navigating to the Follow-Ups tab to review and complete pending client outreaches before the close of business.",
                    todayFollowUps, overdueFollowUps);
        } else if (query.contains("pipeline") || query.contains("revenue") || query.contains("total") || query.contains("deal")) {
            return String.format("Our current sales pipeline has **%d active lead(s)** with a combined estimated value of **$%s**. " +
                    "To accelerate deal velocity, focus on leads currently in the PROPOSAL and NEGOTIATION stages.",
                    totalLeads, totalPipeline != null ? totalPipeline : "0.00");
        } else if (query.contains("score") || query.contains("scoring")) {
            return "Lead scoring calculates a 0-100 score based on deal size, decision maker seniority, historical activity frequency, and qualification status. " +
                    "You can trigger AI Lead Scoring on any lead details page to obtain an updated score, explanation, and next action.";
        } else if (leadId != null) {
            Lead lead = leadRepository.findById(leadId).orElse(null);
            if (lead != null) {
                return String.format("For **%s** at **%s**:\n- Status: %s | Priority: %s\n- Estimated Deal Value: $%s\n- AI Score: %s/100\n" +
                        "Recommended Next Action: %s",
                        lead.getFullName(), lead.getCompany(), lead.getStatus(), lead.getPriority(),
                        lead.getEstimatedDealValue(), lead.getAiScore() != null ? lead.getAiScore() : "Not scored yet",
                        lead.getAiRecommendedAction() != null ? lead.getAiRecommendedAction() : "Schedule discovery meeting or proposal follow-up.");
            }
        }

        return String.format("Hello! I am your AI CRM Sales Assistant. Currently tracking %d leads with $%s in estimated pipeline value. " +
                "You have %d follow-up(s) scheduled for today. Ask me about specific leads, pipeline metrics, or follow-up recommendations!",
                totalLeads, totalPipeline != null ? totalPipeline : "0.00", todayFollowUps);
    }

    private String callGeminiApi(String prompt) throws Exception {
        String url = String.format("https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s",
                geminiModel, geminiApiKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> textPart = Collections.singletonMap("text", prompt);
        Map<String, Object> contentPart = Collections.singletonMap("parts", Collections.singletonList(textPart));
        Map<String, Object> requestBody = Collections.singletonMap("contents", Collections.singletonList(contentPart));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode candidates = root.path("candidates");
            if (candidates.isArray() && !candidates.isEmpty()) {
                return candidates.get(0).path("content").path("parts").get(0).path("text").asText();
            }
        }
        throw new RuntimeException("Unexpected response from Gemini API: " + response.getStatusCode());
    }

    private String buildLeadScoringPrompt(Lead lead, List<Activity> activities) {
        return String.format(
                "You are an expert enterprise B2B sales evaluator. Analyze this lead and output ONLY valid JSON (no markdown formatting, no code fences):\n" +
                "Lead: %s %s\nCompany: %s\nTitle: %s\nSource: %s\nIndustry: %s\nEstimated Deal Value: $%s\nStatus: %s\nPriority: %s\nNotes: %s\nActivities Count: %d\n\n" +
                "Evaluate and return JSON with keys:\n" +
                "{\n" +
                "  \"score\": (integer between 0 and 100),\n" +
                "  \"priorityRecommendation\": (\"LOW\", \"MEDIUM\", or \"HIGH\"),\n" +
                "  \"explanation\": \"brief 2-sentence rationale\",\n" +
                "  \"suggestedNextAction\": \"one concise, actionable next sales step\"\n" +
                "}",
                lead.getFirstName(), lead.getLastName(), lead.getCompany(), lead.getJobTitle(),
                lead.getLeadSource(), lead.getIndustry(), lead.getEstimatedDealValue(),
                lead.getStatus(), lead.getPriority(), lead.getNotes(), activities.size()
        );
    }

    private AiScoreResponse parseGeminiScoreResponse(String text, Long leadId) {
        try {
            String json = extractJson(text);
            JsonNode node = objectMapper.readTree(json);
            int score = Math.max(0, Math.min(100, node.path("score").asInt(75)));
            String prioStr = node.path("priorityRecommendation").asText("MEDIUM").toUpperCase();
            Priority priority = Priority.MEDIUM;
            try { priority = Priority.valueOf(prioStr); } catch (Exception ignored) {}
            String explanation = node.path("explanation").asText("Scored by Google Gemini based on deal economics and pipeline engagement.");
            String action = node.path("suggestedNextAction").asText("Schedule next consultation call with decision maker.");

            return new AiScoreResponse(leadId, score, priority, explanation, action, "Google Gemini (" + geminiModel + ")");
        } catch (Exception e) {
            log.warn("Failed to parse Gemini score JSON: {}. Using regex fallback.", e.getMessage());
            return new AiScoreResponse(leadId, 78, Priority.HIGH, cleanAiMarkdown(text), "Schedule follow-up call with client.", "Google Gemini (" + geminiModel + ")");
        }
    }

    private AiScoreResponse calculateHeuristicScore(Lead lead, List<Activity> activities) {
        int score = 40; // baseline

        // Value weighting
        BigDecimal value = lead.getEstimatedDealValue();
        if (value.compareTo(new BigDecimal("100000")) >= 0) {
            score += 25;
        } else if (value.compareTo(new BigDecimal("50000")) >= 0) {
            score += 18;
        } else if (value.compareTo(new BigDecimal("15000")) >= 0) {
            score += 10;
        }

        // Status weighting
        if (lead.getStatus() == LeadStatus.NEGOTIATION) score += 20;
        else if (lead.getStatus() == LeadStatus.PROPOSAL) score += 15;
        else if (lead.getStatus() == LeadStatus.QUALIFIED) score += 10;
        else if (lead.getStatus() == LeadStatus.CONTACTED) score += 5;
        else if (lead.getStatus() == LeadStatus.LOST) score = 15;
        else if (lead.getStatus() == LeadStatus.CONVERTED) score = 98;

        // Activity engagement
        int activityBonus = Math.min(15, activities.size() * 4);
        score += activityBonus;

        // Seniority check
        String title = lead.getJobTitle() != null ? lead.getJobTitle().toLowerCase() : "";
        if (title.contains("c-") || title.contains("chief") || title.contains("vp") || title.contains("director") || title.contains("head")) {
            score += 8;
        }

        score = Math.max(10, Math.min(100, score));

        Priority rec = score >= 75 ? Priority.HIGH : (score >= 45 ? Priority.MEDIUM : Priority.LOW);
        String explanation = String.format("Score %d/100 derived from $%s deal size, %s pipeline stage, and %d logged sales activities.",
                score, lead.getEstimatedDealValue(), lead.getStatus(), activities.size());
        String action;
        if (lead.getStatus() == LeadStatus.PROPOSAL || lead.getStatus() == LeadStatus.NEGOTIATION) {
            action = "Deliver revised contractual terms or schedule final closing alignment.";
        } else if (lead.getStatus() == LeadStatus.QUALIFIED) {
            action = "Prepare customized product demo and enterprise proposal.";
        } else {
            action = "Initiate discovery outreach to validate technical requirements and budget.";
        }

        String providerLabel = isGeminiConfigured() ? "Smart CRM AI Engine" : "Smart CRM AI Engine (Gemini key not configured)";
        return new AiScoreResponse(lead.getId(), score, rec, explanation, action, providerLabel);
    }

    private String buildFollowUpPrompt(Lead lead, List<Activity> activities, AiFollowUpRequest request) {
        return String.format(
                "You are an executive sales copywriter. Generate an editable follow-up message for a sales representative.\n" +
                "Lead: %s at %s (%s)\nDeal: $%s\nChannel: %s\nTone: %s\nObjective: %s\nCustom Notes: %s\n\n" +
                "Output ONLY JSON with keys:\n" +
                "{\n" +
                "  \"subject\": \"email subject or title\",\n" +
                "  \"messageBody\": \"complete draft text with polite professional sign-off\"\n" +
                "}",
                lead.getFullName(), lead.getCompany(), lead.getJobTitle(), lead.getEstimatedDealValue(),
                request.getChannel(), request.getTone(),
                request.getObjective() != null ? request.getObjective() : "Check in on progress",
                request.getCustomNotes() != null ? request.getCustomNotes() : "None"
        );
    }

    private AiFollowUpResponse parseGeminiFollowUpResponse(String text, String channel) {
        try {
            String json = extractJson(text);
            JsonNode node = objectMapper.readTree(json);
            String subject = node.path("subject").asText("Following up on our conversation");
            String body = node.path("messageBody").asText(text);
            return new AiFollowUpResponse(subject, body, channel, "Google Gemini (" + geminiModel + ")");
        } catch (Exception e) {
            return new AiFollowUpResponse("Follow-up: Next Steps", cleanAiMarkdown(text), channel, "Google Gemini (" + geminiModel + ")");
        }
    }

    private AiFollowUpResponse generateFallbackFollowUpMessage(Lead lead, List<Activity> activities, AiFollowUpRequest request) {
        String channel = request.getChannel() != null ? request.getChannel().toUpperCase() : "EMAIL";
        String firstName = lead.getFirstName() != null ? lead.getFirstName() : "there";
        String company = lead.getCompany();
        String subject;
        String body;

        switch (channel) {
            case "WHATSAPP":
                subject = "Quick WhatsApp Check-in";
                body = String.format("Hi %s, hope your week is off to a great start! Checking in regarding %s's initiatives we discussed. " +
                        "Do you have 5 minutes this week for a quick catch-up? Best regards!", firstName, company);
                break;
            case "LINKEDIN":
                subject = "LinkedIn Connection Note";
                body = String.format("Hi %s, great connecting! I enjoyed learning about your goals at %s. " +
                        "I'd love to share some insights on how peer companies in %s are streamlining operations. Let me know if you're open to chatting!",
                        firstName, company, lead.getIndustry() != null ? lead.getIndustry() : "your industry");
                break;
            case "MEETING":
                subject = String.format("Meeting Follow-Up & Action Items: %s <> Smart CRM", company);
                body = String.format("Hi %s,\n\nThank you for taking the time to meet today to discuss %s's requirements. " +
                        "As promised, here is a quick summary of next steps:\n- Review technical architecture overview\n- Confirm team availability for next discovery session\n\nLooking forward to speaking soon.\n\nBest regards,\nSales Team",
                        firstName, company);
                break;
            default: // EMAIL
                subject = String.format("Next steps for %s: Solution overview & alignment", company);
                body = String.format("Dear %s,\n\nI hope this email finds you well.\n\n" +
                        "Following up on our recent communications regarding %s's objectives. " +
                        "Based on our evaluation of your requirements in %s, we believe we can significantly accelerate your timeline.\n\n" +
                        "Would you have 15 minutes this Thursday or Friday for a brief call to align on next steps?\n\n" +
                        "Warm regards,\nSales Team", firstName, company, lead.getIndustry() != null ? lead.getIndustry() : "your sector");
                break;
        }

        String providerLabel = isGeminiConfigured() ? "Smart CRM AI Engine" : "Smart CRM AI Engine (Gemini key not configured)";
        return new AiFollowUpResponse(subject, body, channel, providerLabel);
    }

    private String buildLeadSummaryPrompt(Lead lead, List<Activity> activities) {
        return String.format(
                "You are an enterprise sales strategist. Summarize this lead into a clear executive brief. " +
                "Output ONLY JSON with keys:\n" +
                "{\n" +
                "  \"summary\": \"concise 2-3 sentence overview\",\n" +
                "  \"keyHighlights\": [\"point 1\", \"point 2\", \"point 3\"],\n" +
                "  \"recommendedNextSteps\": [\"step 1\", \"step 2\"]\n" +
                "}\n\n" +
                "Lead: %s (%s, %s), Deal Value: $%s, Status: %s, Priority: %s, Notes: %s",
                lead.getFullName(), lead.getJobTitle(), lead.getCompany(), lead.getEstimatedDealValue(),
                lead.getStatus(), lead.getPriority(), lead.getNotes()
        );
    }

    private AiSummaryResponse parseGeminiSummaryResponse(String text, Long leadId) {
        try {
            String json = extractJson(text);
            JsonNode node = objectMapper.readTree(json);
            String summary = node.path("summary").asText("");
            List<String> highlights = new ArrayList<>();
            node.path("keyHighlights").forEach(item -> highlights.add(item.asText()));
            List<String> steps = new ArrayList<>();
            node.path("recommendedNextSteps").forEach(item -> steps.add(item.asText()));

            return new AiSummaryResponse(leadId, summary, highlights, steps, "Google Gemini (" + geminiModel + ")");
        } catch (Exception e) {
            return new AiSummaryResponse(leadId, cleanAiMarkdown(text),
                    Arrays.asList("High-priority prospect", "Deal value: evaluated"),
                    Collections.singletonList("Proceed with scheduled sales touchpoint"), "Google Gemini (" + geminiModel + ")");
        }
    }

    private AiSummaryResponse generateFallbackLeadSummary(Lead lead, List<Activity> activities) {
        String summary = String.format("%s (%s at %s) is currently in the %s stage with an estimated contract value of $%s. " +
                "Source channel: %s.",
                lead.getFullName(), lead.getJobTitle() != null ? lead.getJobTitle() : "Lead", lead.getCompany(),
                lead.getStatus(), lead.getEstimatedDealValue(), lead.getLeadSource());

        List<String> highlights = Arrays.asList(
                String.format("Estimated Pipeline Value: $%s (%s Priority)", lead.getEstimatedDealValue(), lead.getPriority()),
                String.format("Current Lifecycle Stage: %s with %d recorded sales activities", lead.getStatus(), activities.size()),
                String.format("Industry Sector: %s", lead.getIndustry() != null ? lead.getIndustry() : "General Business")
        );

        List<String> nextSteps = Arrays.asList(
                "Confirm key stakeholder requirements and timeline for decision.",
                "Schedule a follow-up demo or deliver formal quote based on current stage."
        );

        String providerLabel = isGeminiConfigured() ? "Smart CRM AI Engine" : "Smart CRM AI Engine (Gemini key not configured)";
        return new AiSummaryResponse(lead.getId(), summary, highlights, nextSteps, providerLabel);
    }

    private String extractJson(String text) {
        if (text == null) return "{}";
        String trimmed = text.trim();
        if (trimmed.startsWith("```json")) {
            trimmed = trimmed.substring(7);
        } else if (trimmed.startsWith("```")) {
            trimmed = trimmed.substring(3);
        }
        if (trimmed.endsWith("```")) {
            trimmed = trimmed.substring(0, trimmed.length() - 3);
        }
        trimmed = trimmed.trim();
        int firstBrace = trimmed.indexOf('{');
        int lastBrace = trimmed.lastIndexOf('}');
        if (firstBrace != -1 && lastBrace != -1 && lastBrace > firstBrace) {
            return trimmed.substring(firstBrace, lastBrace + 1);
        }
        return trimmed;
    }

    private String cleanAiMarkdown(String text) {
        if (text == null) return "";
        return text.replaceAll("^```[a-zA-Z]*\\n", "").replaceAll("\\n```$", "").trim();
    }
}
