package com.isiflix.events_management_api.integration.errors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isiflix.events_management_api.app.errors.ErrorResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ErrorsControllerTest {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @Autowired
    public ErrorsControllerTest(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    private void assertErrorResponse(String responseBody, String errorCode) {
        Assertions.assertDoesNotThrow(() -> {
            final var error = objectMapper.readValue(responseBody, ErrorResponse.class);
            assertEquals(errorCode, error.code());
            assertTrue(OffsetDateTime.now().isAfter(error.moment()));
            assertFalse(error.message().isBlank());
            assertNull(error.issues());
        });
    }

    @Test
    @DisplayName("Integration Test - Non existing route")
    public void shouldReturnNotFoundForNonExistingRoute() throws Exception {
        final var responseBody = mockMvc.perform(get("/non-existing-route"))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertErrorResponse(responseBody, "not-found");
    }

    @Test
    @DisplayName("Integration Test - Existing route but not supported method")
    public void shouldReturnNotFoundForNotSupportedMethod() throws Exception {
        final var responseBody = mockMvc.perform(put("/events"))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertErrorResponse(responseBody, "not-found");
    }

    @Test
    @DisplayName("Integration Test - Existing route but unreadable body")
    public void shouldReturnUnprocessableEntityForNotReadableBody() throws Exception {
        final var request = post("/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{");

        final var responseBody = mockMvc.perform(request)
                .andExpect(status().isUnprocessableEntity())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertErrorResponse(responseBody, "not-readable-payload");
    }
}
