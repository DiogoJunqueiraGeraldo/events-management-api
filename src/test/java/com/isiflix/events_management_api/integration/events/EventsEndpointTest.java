package com.isiflix.events_management_api.integration.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isiflix.events_management_api.app.errors.ErrorResponse;
import com.isiflix.events_management_api.app.events.controllers.CreateEventRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class EventsEndpointTest {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Autowired
    public EventsEndpointTest(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @Test
    @DisplayName("Integration Test - Create new event")
    public void shouldCreateNewEvent() throws Exception {
        final var tomorrow = LocalDate.now().plusDays(1);
        final var forFree = BigDecimal.ZERO;

        final var event = new CreateEventRequest(
                "Saturday IsiFlix Live Coding!",
                "online",
                forFree,
                tomorrow,
                tomorrow,
                LocalTime.of(8, 0),
                LocalTime.of(18, 0)
        );

        final var expectedStartDate = event.startDate().format(dateFormatter);
        final var expectedEndDate = event.endDate().format(dateFormatter);
        final var expectedStartTime = event.startTime().format(timeFormatter);
        final var expectedEndTime = event.endTime().format(timeFormatter);

        final var payload = objectMapper.writeValueAsString(event);
        final var request = post("/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload);

        this.mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value(event.name()))
                .andExpect(jsonPath("$.prettyName").isString())
                .andExpect(jsonPath("$.location").value(event.location()))
                .andExpect(jsonPath("$.price").value(event.price()))
                .andExpect(jsonPath("$.startDate").value(expectedStartDate))
                .andExpect(jsonPath("$.endDate").value(expectedEndDate))
                .andExpect(jsonPath("$.startTime").value(expectedStartTime))
                .andExpect(jsonPath("$.endTime").value(expectedEndTime));
    }

    @Test
    @DisplayName("Integration Test - Invalid event - Missing required fields")
    public void shouldValidateRequiredFields() throws Exception {
        final var request = post("/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}");

        final var responseBody = this.mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        final var error = objectMapper.readValue(responseBody, ErrorResponse.class);

        assertEquals("invalid-payload", error.code());
        assertTrue(OffsetDateTime.now().isAfter(error.moment()));
        assertFalse(error.message().isBlank());
        assertFalse(error.issues().isEmpty());
        List.of("name", "location", "price", "startDate", "endDate", "startTime", "endTime")
                .forEach((fieldName) -> assertTrue(error.issues().containsKey(fieldName)));
    }

    @Test
    @DisplayName("Integration Test - Invalid event - Incoherent fields")
    public void shouldValidateFieldsCoherence() throws Exception {
        final var yesterday = OffsetDateTime.now().minusDays(1).toLocalDate();
        final var event = new CreateEventRequest(
                "",
                "",
                BigDecimal.valueOf(-100.00),
                yesterday,
                yesterday,
                LocalTime.of(8, 0),
                LocalTime.of(18, 0)
        );

        final var payload = objectMapper.writeValueAsString(event);
        final var request = post("/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload);

        final var responseBody = mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        final var error = objectMapper.readValue(responseBody, ErrorResponse.class);

        assertEquals("invalid-payload", error.code());
        assertTrue(OffsetDateTime.now().isAfter(error.moment()));
        assertFalse(error.message().isBlank());
        assertFalse(error.issues().isEmpty());
        List.of("name", "location", "price", "startDate", "endDate")
                .forEach((fieldName) -> assertTrue(error.issues().containsKey(fieldName)));
    }
}
