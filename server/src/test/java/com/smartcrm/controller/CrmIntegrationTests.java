package com.smartcrm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartcrm.dto.auth.LoginRequest;
import com.smartcrm.dto.auth.RegisterRequest;
import com.smartcrm.dto.lead.LeadRequest;
import com.smartcrm.entity.enums.LeadSource;
import com.smartcrm.entity.enums.LeadStatus;
import com.smartcrm.entity.enums.Priority;
import com.smartcrm.entity.enums.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CrmIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testSystemHealth() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("UP"));
    }

    @Test
    void testAuthenticationAndLeadLifecycle() throws Exception {
        // 1. Register a test user
        RegisterRequest registerReq = new RegisterRequest("Test Rep", "rep.test@smartcrm.com", "Password123!", Role.SALES_REP);
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andExpect(jsonPath("$.data.user.email").value("rep.test@smartcrm.com"));

        // 2. Login with valid credentials
        LoginRequest loginReq = new LoginRequest("rep.test@smartcrm.com", "Password123!");
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andReturn();

        String responseJson = loginResult.getResponse().getContentAsString();
        String token = objectMapper.readTree(responseJson).path("data").path("token").asText();

        // 3. Create a new lead as authenticated user
        LeadRequest leadReq = new LeadRequest();
        leadReq.setFirstName("Oliver");
        leadReq.setLastName("Queen");
        leadReq.setCompany("Star City Tech");
        leadReq.setEmail("oliver@starcity.com");
        leadReq.setPhone("+1-555-9988");
        leadReq.setJobTitle("CEO");
        leadReq.setLeadSource(LeadSource.WEBSITE);
        leadReq.setIndustry("Defense Tech");
        leadReq.setEstimatedDealValue(new BigDecimal("85000.00"));
        leadReq.setStatus(LeadStatus.NEW);
        leadReq.setPriority(Priority.HIGH);
        leadReq.setNotes("High interest in security integration");

        MvcResult leadResult = mockMvc.perform(post("/api/leads")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(leadReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.fullName").value("Oliver Queen"))
                .andReturn();

        long createdLeadId = objectMapper.readTree(leadResult.getResponse().getContentAsString())
                .path("data").path("id").asLong();

        // 4. Fetch the created lead
        mockMvc.perform(get("/api/leads/" + createdLeadId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.company").value("Star City Tech"));

        // 5. Test AI Lead Scoring
        mockMvc.perform(post("/api/ai/lead-score")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("{\"leadId\": %d}", createdLeadId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.score").isNumber())
                .andExpect(jsonPath("$.data.explanation").isNotEmpty());

        // 6. Test Dashboard Summary
        mockMvc.perform(get("/api/dashboard/summary")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalLeads").isNumber());
    }
}
