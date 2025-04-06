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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ErrorHandlingIntegrationTest {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @Autowired
    public ErrorHandlingIntegrationTest(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    private void assertErrorResponseWithoutIssues(String responseBody, String errorCode) {
        Assertions.assertDoesNotThrow(() -> {
            final var error = objectMapper.readValue(responseBody, ErrorResponse.class);
            assertEquals(errorCode, error.code());
            assertTrue(OffsetDateTime.now().isAfter(error.moment()));
            assertFalse(error.message().isBlank());
            assertNull(error.issues());
        });
    }

    private void assertInvalidParameters(String responseBody, List<String> issues) {
        Assertions.assertDoesNotThrow(() -> {
            final var error = objectMapper.readValue(responseBody, ErrorResponse.class);
            assertEquals("invalid-parameters", error.code());
            assertTrue(OffsetDateTime.now().isAfter(error.moment()));
            assertFalse(error.message().isBlank());
            issues.forEach(issueName -> Assertions.assertTrue(error.issues().containsKey(issueName)));
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

        assertErrorResponseWithoutIssues(responseBody, "not-found");
    }

    @Test
    @DisplayName("Integration Test - Existing route but not supported method")
    public void shouldReturnNotFoundForNotSupportedMethod() throws Exception {
        final var responseBody = mockMvc.perform(put("/events"))
                .andExpect(status().isMethodNotAllowed())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertErrorResponseWithoutIssues(responseBody, "method-not-allowed");
    }

    @Test
    @DisplayName("Integration Test - Existing route but unreadable body")
    public void shouldReturnUnprocessableEntityForNotReadableBody() throws Exception {
        final var invalidJson = "{";
        final var request = post("/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson);

        final var responseBody = mockMvc.perform(request)
                .andExpect(status().isUnprocessableEntity())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertErrorResponseWithoutIssues(responseBody, "not-readable-payload");
    }

    @Test
    @DisplayName("Integration Test - Parameter validation error")
    public void shouldReturnBadRequestForMethodValidation() throws Exception {
        final var request = get("/events")
                .contentType(MediaType.APPLICATION_JSON)
                .queryParam("page", "-123")
                .queryParam("size", "-321");

        final var responseBody = mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertInvalidParameters(
                responseBody,
                List.of("page", "size")
        );
    }

    @Test
    @DisplayName("Integration Test - Parameter conversion error")
    public void shouldReturnBadRequestForArgumentTypeMismatch() throws Exception {
        final var request = get("/events")
                .contentType(MediaType.APPLICATION_JSON)
                .queryParam("page", "abc")
                .queryParam("size", "bca");

        final var responseBody = mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertInvalidParameters(
                responseBody,
                // short circuit at the first exception
                List.of("page")
        );
    }
}
