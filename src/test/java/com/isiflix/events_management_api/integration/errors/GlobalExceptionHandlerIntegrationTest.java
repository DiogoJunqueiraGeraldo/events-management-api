package com.isiflix.events_management_api.integration.errors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isiflix.events_management_api.app.errors.StandardErrorResponse;
import com.isiflix.events_management_api.app.errors.GlobalExceptionHandler;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
public class GlobalExceptionHandlerIntegrationTest {
    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    private void assertErrorResponseWithoutIssues(String responseBody, String errorCode) {
        Assertions.assertDoesNotThrow(() -> {
            final var error = objectMapper.readValue(responseBody, StandardErrorResponse.class);
            assertThat(error.code()).isEqualTo(errorCode);
            assertThat(OffsetDateTime.now()).isAfter(error.moment());
            assertThat(error.message()).isNotBlank();
            assertThat(error.issues()).isNull();
        });
    }

    private void assertInvalidParameters(String responseBody, List<String> expectedIssues) {
        Assertions.assertDoesNotThrow(() -> {
            final var error = objectMapper.readValue(responseBody, StandardErrorResponse.class);
            assertThat(error.code()).isEqualTo("invalid-parameters");
            assertThat(OffsetDateTime.now()).isAfter(error.moment());
            assertThat(error.message()).isNotBlank();
            assertThat(error.issues()).containsOnlyKeys(expectedIssues);
        });
    }

    @Test
    @DisplayName("Integration Test - Unexpected Failure - Log Level")
    public void shouldLogErrorLevelLogsForUnexpectedFailure() throws Exception {
        try (final LogCaptor logCaptor = LogCaptor.forClass(GlobalExceptionHandler.class)) {
            final var responseBody = mockMvc.perform(get("/tests/simulate/unexpected-failure"))
                    .andExpect(status().isInternalServerError())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            final var response = objectMapper.readValue(responseBody, StandardErrorResponse.class);
            assertThat(response.code()).isEqualTo("unexpected-error");
            assertThat(response.message()).isNotBlank();
            assertThat(logCaptor.getErrorLogs()).hasSize(1);
        }
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
