package com.campus.certificate;

import com.campus.certificate.repository.CertificateRepository;
import com.campus.certificate.repository.StudentUserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CertificateFlowIntegrationTest {
    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired CertificateRepository certificateRepository;
    @Autowired StudentUserRepository userRepository;

    @BeforeEach
    void clean() {
        certificateRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void completeStudentCertificateFlow() throws Exception {
        String token = registerAndGetToken();

        mockMvc.perform(get("/api/certificates"))
                .andExpect(status().isUnauthorized());

        Map<String, Object> certificate = new LinkedHashMap<>();
        certificate.put("name", "河南省大学生创新创业大赛一等奖");
        certificate.put("issuer", "河南省教育厅");
        certificate.put("category", "INNOVATION_ENTREPRENEURSHIP");
        certificate.put("level", "NATIONAL");
        certificate.put("awardType", "TEAM");
        certificate.put("issueDate", LocalDate.now().minusDays(10).toString());
        certificate.put("credentialNo", "HN-IE-2026-001");
        certificate.put("description", "大学生创新创业项目荣誉");

        String created = mockMvc.perform(post("/api/certificates")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(certificate)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("河南省大学生创新创业大赛一等奖"))
                .andExpect(jsonPath("$.awardType").value("TEAM"))
                .andReturn().getResponse().getContentAsString();

        long id = objectMapper.readTree(created).get("id").asLong();
        mockMvc.perform(get("/api/certificates")
                        .header("Authorization", "Bearer " + token)
                        .param("keyword", "创新创业")
                        .param("category", "INNOVATION_ENTREPRENEURSHIP"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content", hasSize(1)));

        mockMvc.perform(get("/api/dashboard").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.thisYear").value(1))
                .andExpect(jsonPath("$.categories[0].category").value("INNOVATION_ENTREPRENEURSHIP"))
                .andExpect(jsonPath("$.levels[0].level").value("NATIONAL"))
                .andExpect(jsonPath("$.awardTypes[0].awardType").value("TEAM"));

        mockMvc.perform(delete("/api/certificates/{id}", id).header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/certificates/{id}", id).header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    private String registerAndGetToken() throws Exception {
        Map<String, String> registration = Map.of(
                "name", "林知夏",
                "studentId", "20260001",
                "email", "lin@example.edu.cn",
                "password", "Campus2026"
        );
        String body = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registration)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.user.studentId").value("20260001"))
                .andReturn().getResponse().getContentAsString();
        JsonNode json = objectMapper.readTree(body);
        return json.get("token").asText();
    }
}
