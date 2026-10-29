package com.smartcrm.config;

import com.smartcrm.entity.*;
import com.smartcrm.entity.enums.*;
import com.smartcrm.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final LeadRepository leadRepository;
    private final ActivityRepository activityRepository;
    private final FollowUpRepository followUpRepository;
    private final LeadStatusHistoryRepository statusHistoryRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${smartcrm.admin.email:admin@smartcrm.com}")
    private String adminEmail;

    @Value("${smartcrm.admin.password:Admin@123CRM!}")
    private String adminPassword;

    @Value("${smartcrm.admin.name:System Administrator}")
    private String adminName;

    public DataInitializer(UserRepository userRepository,
                           LeadRepository leadRepository,
                           ActivityRepository activityRepository,
                           FollowUpRepository followUpRepository,
                           LeadStatusHistoryRepository statusHistoryRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.leadRepository = leadRepository;
        this.activityRepository = activityRepository;
        this.followUpRepository = followUpRepository;
        this.statusHistoryRepository = statusHistoryRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        // Initialize default admin if no users exist
        if (userRepository.count() == 0) {
            log.info("Creating default system administrator: {}", adminEmail);
            User admin = new User(adminEmail, passwordEncoder.encode(adminPassword), adminName, Role.ADMIN);
            admin = userRepository.save(admin);

            log.info("Creating default sales representative: alex.sales@smartcrm.com");
            User salesRep = new User("alex.sales@smartcrm.com", passwordEncoder.encode("Sales@123CRM!"), "Alex Morgan", Role.SALES_REP);
            salesRep = userRepository.save(salesRep);

            // Populate initial realistic business leads
            seedDemoData(admin, salesRep);
        }
    }

    private void seedDemoData(User admin, User rep) {
        log.info("Seeding realistic CRM demo leads and activities...");

        // Lead 1: CloudTech Solutions - PROPOSAL
        Lead l1 = createLead("Sarah", "Connor", "Acme Cloud Corp", "sarah.c@acmecloud.io", "+1-555-0101",
                "VP of Engineering", LeadSource.WEBSITE, "Enterprise SaaS", new BigDecimal("75000.00"),
                LeadStatus.PROPOSAL, Priority.HIGH, admin,
                "Evaluating cloud migration and multi-region failover. Sent enterprise proposal.",
                88, "High deal value, executive sponsor engaged, urgent timeline.",
                "Schedule contract review call with CFO.");

        // Lead 2: Nexus Financial - QUALIFIED
        Lead l2 = createLead("David", "Kim", "Nexus Financial Group", "dkim@nexusfin.com", "+1-555-0102",
                "Chief Technology Officer", LeadSource.LINKEDIN, "FinTech", new BigDecimal("120000.00"),
                LeadStatus.QUALIFIED, Priority.HIGH, rep,
                "Looking to replace legacy payment gateway with modern API suite.",
                92, "Budget approved, immediate compliance deadline.",
                "Deliver security architecture and SOC2 certification briefing.");

        // Lead 3: Apex Health - NEGOTIATION
        Lead l3 = createLead("Elena", "Rostova", "Apex Health Systems", "elena@apexhealth.org", "+1-555-0103",
                "Director of Clinical Systems", LeadSource.REFERRAL, "Healthcare", new BigDecimal("95000.00"),
                LeadStatus.NEGOTIATION, Priority.HIGH, admin,
                "In final redlining on SLA and data residency terms.",
                94, "Final procurement stage. Legal reviewing warranty terms.",
                "Send revised master services agreement with legal team.");

        // Lead 4: BlueWave Logistics - CONTACTED
        Lead l4 = createLead("Marcus", "Vance", "BlueWave Freight Inc", "mvance@bluewave.com", "+1-555-0104",
                "Fleet Operations Manager", LeadSource.AD_CAMPAIGN, "Logistics & Supply Chain", new BigDecimal("35000.00"),
                LeadStatus.CONTACTED, Priority.MEDIUM, rep,
                "Requested demo of real-time GPS tracking integration.",
                68, "Mid-size fleet looking for efficiency gains.",
                "Send product interactive walkthrough demo link.");

        // Lead 5: Vertex AI - CONVERTED
        Lead l5 = createLead("Chloe", "Bennett", "Vertex Neural Systems", "cbennett@vertexai.tech", "+1-555-0105",
                "Head of AI Products", LeadSource.WEBSITE, "Artificial Intelligence", new BigDecimal("150000.00"),
                LeadStatus.CONVERTED, Priority.HIGH, admin,
                "Contract signed. 3-year enterprise license with custom ML pipeline.",
                98, "Customer successfully onboarded.",
                "Initiate kick-off technical onboarding session.");

        // Lead 6: GreenPeak Energy - NEW
        Lead l6 = createLead("Jonathan", "Taylor", "GreenPeak Clean Power", "jtaylor@greenpeak.energy", "+1-555-0106",
                "Sustainability Lead", LeadSource.EMAIL, "Renewable Energy", new BigDecimal("45000.00"),
                LeadStatus.NEW, Priority.MEDIUM, rep,
                "Submitted contact form regarding ESG reporting automation module.",
                62, "Early inbound inquiry from corporate energy player.",
                "Initiate introductory qualification call.");

        // Lead 7: CyberGuard Labs - LOST
        Lead l7 = createLead("Rachel", "Zheng", "CyberGuard Security", "rzheng@cyberguard.net", "+1-555-0107",
                "CISO", LeadSource.PHONE, "Cybersecurity", new BigDecimal("50000.00"),
                LeadStatus.LOST, Priority.LOW, rep,
                "Selected an internal custom-built tool due to strict on-premise mandate.",
                25, "Closed-lost due to non-negotiable on-prem mandate.",
                "Add to quarterly newsletter list for future outreach.");

        // Lead 8: Swift Retailers - NEW
        Lead l8 = createLead("Liam", "Smith", "Swift Omni Retail", "liam@swiftretail.com", "+1-555-0108",
                "Head of E-Commerce", LeadSource.WEBSITE, "E-Commerce", new BigDecimal("28000.00"),
                LeadStatus.NEW, Priority.MEDIUM, admin,
                "Looking to modernize POS synchronization and inventory analytics.",
                58, "Solid retail prospect needing inventory unification.",
                "Send introductory case study on retail automation.");

        // Add activities
        addActivity(l1, admin, ActivityType.CALL, "Discovery Call with Engineering Leadership",
                "Discussed architecture roadmap, 99.99% uptime requirements, and integration with AWS.", LocalDateTime.now().minusDays(3));
        addActivity(l1, admin, ActivityType.EMAIL, "Sent Enterprise Proposal Deck & Pricing",
                "Shared formal PDF proposal with customized discount for 2-year commitment.", LocalDateTime.now().minusDays(1));

        addActivity(l2, rep, ActivityType.MEETING, "Technical Deep-Dive Architecture Review",
                "Demoed REST APIs and webhook response times. Client was impressed with sub-50ms latency.", LocalDateTime.now().minusDays(2));

        addActivity(l3, admin, ActivityType.CALL, "Contract Negotiation with Procurement",
                "Agreed on payment terms (Net 30) and annual renewal clause.", LocalDateTime.now().minusHours(5));

        addActivity(l5, admin, ActivityType.MEETING, "Contract Signing Celebration & Handoff",
                "Official signing complete. Introduced customer success manager for week 1 onboarding.", LocalDateTime.now().minusDays(4));

        // Add Follow-ups
        addFollowUp(l1, admin, "Send follow-up on enterprise proposal terms",
                "Follow up on decision timeline after proposal review.", LocalDateTime.now().plusHours(4), FollowUpStatus.PENDING);

        addFollowUp(l2, rep, "Review SOC2 compliance questions with SecOps team",
                "David requested clarification on encryption at rest keys.", LocalDateTime.now().minusHours(2), FollowUpStatus.PENDING); // Overdue test

        addFollowUp(l3, admin, "Deliver finalized countersigned contract",
                "Receive signed copy from CEO and send counter-signature.", LocalDateTime.now().plusDays(2), FollowUpStatus.PENDING);

        addFollowUp(l4, rep, "Confirm product demo attendance for Marcus",
                "Send calendar invite and Zoom link for Tuesday demo.", LocalDateTime.now().plusDays(1), FollowUpStatus.PENDING);

        addFollowUp(l6, rep, "First outreach call to Jonathan Taylor",
                "Introduce CRM capabilities for renewable energy portfolios.", LocalDateTime.now().plusHours(2), FollowUpStatus.PENDING);

        log.info("CRM demo data initialized successfully!");
    }

    private Lead createLead(String first, String last, String company, String email, String phone,
                            String title, LeadSource source, String industry, BigDecimal value,
                            LeadStatus status, Priority priority, User assigned, String notes,
                            int aiScore, String explanation, String action) {
        Lead lead = new Lead();
        lead.setFirstName(first);
        lead.setLastName(last);
        lead.setFullName(first + " " + last);
        lead.setCompany(company);
        lead.setEmail(email);
        lead.setPhone(phone);
        lead.setJobTitle(title);
        lead.setLeadSource(source);
        lead.setIndustry(industry);
        lead.setEstimatedDealValue(value);
        lead.setStatus(status);
        lead.setPriority(priority);
        lead.setAssignedTo(assigned);
        lead.setNotes(notes);
        lead.setAiScore(aiScore);
        lead.setAiScoreExplanation(explanation);
        lead.setAiRecommendedAction(action);
        lead.setAiLastScoredAt(LocalDateTime.now().minusDays(1));
        lead = leadRepository.save(lead);

        LeadStatusHistory history = new LeadStatusHistory(lead, assigned, null, status.name(), "Initial lead setup");
        statusHistoryRepository.save(history);

        return lead;
    }

    private void addActivity(Lead lead, User user, ActivityType type, String title, String desc, LocalDateTime time) {
        Activity act = new Activity(lead, user, type, title, desc, time);
        activityRepository.save(act);
    }

    private void addFollowUp(Lead lead, User user, String title, String desc, LocalDateTime due, FollowUpStatus status) {
        FollowUp f = new FollowUp();
        f.setLead(lead);
        f.setAssignedTo(user);
        f.setTitle(title);
        f.setDescription(desc);
        f.setDueDate(due);
        f.setStatus(status);
        followUpRepository.save(f);
    }
}
