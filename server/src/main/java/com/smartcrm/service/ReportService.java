package com.smartcrm.service;

import com.smartcrm.entity.Lead;
import com.smartcrm.repository.LeadRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReportService {

    private final LeadRepository leadRepository;

    public ReportService(LeadRepository leadRepository) {
        this.leadRepository = leadRepository;
    }

    public void exportLeadsCsv(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"leads_export_" + System.currentTimeMillis() + ".csv\"");

        List<Lead> leads = leadRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        PrintWriter writer = response.getWriter();
        // CSV Header
        writer.println("Lead ID,Full Name,Company,Email,Phone,Job Title,Lead Source,Industry,Estimated Deal Value,Status,Priority,Assigned Sales Rep,AI Score,Created Date");

        for (Lead lead : leads) {
            String assignedRep = lead.getAssignedTo() != null ? lead.getAssignedTo().getFullName() : "Unassigned";
            String created = lead.getCreatedAt() != null ? lead.getCreatedAt().format(dtf) : "";

            writer.printf("%d,\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",%.2f,\"%s\",\"%s\",\"%s\",%s,\"%s\"%n",
                    lead.getId(),
                    escapeCsv(lead.getFullName()),
                    escapeCsv(lead.getCompany()),
                    escapeCsv(lead.getEmail()),
                    escapeCsv(lead.getPhone() != null ? lead.getPhone() : ""),
                    escapeCsv(lead.getJobTitle() != null ? lead.getJobTitle() : ""),
                    lead.getLeadSource().name(),
                    escapeCsv(lead.getIndustry() != null ? lead.getIndustry() : ""),
                    lead.getEstimatedDealValue(),
                    lead.getStatus().name(),
                    lead.getPriority().name(),
                    escapeCsv(assignedRep),
                    lead.getAiScore() != null ? lead.getAiScore().toString() : "N/A",
                    created
            );
        }
        writer.flush();
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        return value.replace("\"", "\"\"");
    }
}
