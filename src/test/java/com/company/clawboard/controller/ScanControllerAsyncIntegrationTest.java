package com.company.clawboard.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("ScanController Async Integration Tests")
class ScanControllerAsyncIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/v1/scan/trigger should return 200 when no scan running")
    void testTriggerScan_Success() throws Exception {
        mockMvc.perform(post("/api/v1/scan/trigger")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.scanId").exists())
            .andExpect(jsonPath("$.data.triggerType").value("manual"))
            .andExpect(jsonPath("$.data.status").value("triggered"));
    }

    @Test
    @DisplayName("GET /api/v1/scan/status should return complete status information")
    void testGetStatus() throws Exception {
        mockMvc.perform(get("/api/v1/scan/status")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.scanning").exists())
            // currentScan 和 lastCompletedScan 可能为 null，取决于是否有扫描记录
            .andExpect(jsonPath("$.data.nextScheduledAt").exists());
    }

    @Test
    @DisplayName("GET /api/v1/scan/history should return paginated history")
    void testGetHistory() throws Exception {
        mockMvc.perform(get("/api/v1/scan/history")
                .param("page", "1")
                .param("pageSize", "10")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.total").exists())
            .andExpect(jsonPath("$.data.page").value(1))
            .andExpect(jsonPath("$.data.pageSize").value(10))
            .andExpect(jsonPath("$.data.list").isArray());
    }
}
