package com.modak.notification_service.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.modak.notification_service.NotificationServiceApplication;
import com.modak.notification_service.entities.NotificationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = NotificationServiceApplication.class)
@AutoConfigureMockMvc
public class NotificationServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void integrationTestOk() throws Exception {
        NotificationRequest request = NotificationRequest.builder()
                .message("news 1")
                .type("status")
                .userId("user 1")
                .build();

        String requestBody = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/notifications/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    public void testHandleInvalidNotificationTypeException() throws Exception {

        NotificationRequest request = NotificationRequest.builder()
                .message("prueba")
                .type("Sta")
                .userId("pepe")
                .build();

        String requestBody = objectMapper.writeValueAsString(request);
        mockMvc.perform(post("/notifications/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid notification type"))
                .andExpect(jsonPath("$.description").value("The notification type: Sta is invalid."))
                .andExpect(jsonPath("$.recommendation").isEmpty());
    }
}
