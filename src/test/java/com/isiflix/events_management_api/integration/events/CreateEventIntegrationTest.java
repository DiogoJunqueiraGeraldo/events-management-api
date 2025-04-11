package com.isiflix.events_management_api.integration.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isiflix.events_management_api.app.errors.StandardErrorResponse;
import com.isiflix.events_management_api.app.events.rest.CreateEventRequest;
import com.isiflix.events_management_api.app.events.rest.EventResponse;
import com.isiflix.events_management_api.domain.errors.ViolationCode;
import com.isiflix.events_management_api.infra.database.event.JPAEventRepository;
import com.isiflix.events_management_api.utils.PostgresTestContainerConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(PostgresTestContainerConfiguration.class)
public class CreateEventIntegrationTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final JPAEventRepository jpaEventRepository;

    @Autowired
    public CreateEventIntegrationTest(MockMvc mockMvc, ObjectMapper objectMapper, JPAEventRepository jpaEventRepository) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.jpaEventRepository = jpaEventRepository;
    }

    @BeforeEach
    void setUp() {
        this.jpaEventRepository.deleteAll();
    }

    @Test
    @DisplayName("Integration Test - Create new event")
    public void shouldCreateNewEvent() throws Exception {
        final var tomorrow = LocalDate.now().plusDays(1);
        final var forFree = BigDecimal.ZERO;

        final var createEventRequest = new CreateEventRequest(
                "Saturday IsiFlix Live Coding!",
                "online",
                forFree,
                tomorrow,
                tomorrow,
                LocalTime.of(8, 0),
                LocalTime.of(18, 0)
        );

        final var payload = objectMapper.writeValueAsString(createEventRequest);
        final var request = post("/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload);

        final var responseBody = mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        final var response = objectMapper.readValue(responseBody, EventResponse.class);

        Assertions.assertNotNull(response.id());
        Assertions.assertEquals(createEventRequest.name(), response.name());
        Assertions.assertEquals(createEventRequest.price(), response.price());
        Assertions.assertEquals(createEventRequest.location(), response.location());
        Assertions.assertEquals(createEventRequest.startDate(), response.startDate());
        Assertions.assertEquals(createEventRequest.endDate(), response.endDate());
        Assertions.assertEquals(createEventRequest.startTime(), response.startTime());
        Assertions.assertEquals(createEventRequest.endTime(), response.endTime());
    }

    @Test
    @DisplayName("Integration Test - Conflict - Pretty name already exists")
    public void shouldReturnConflict() throws Exception {
        final var tomorrow = LocalDate.now().plusDays(1);
        final var forFree = BigDecimal.ZERO;
        final var createEventRequest = new CreateEventRequest(
                "Conflicting Pretty Name",
                "online",
                forFree,
                tomorrow,
                tomorrow,
                LocalTime.of(8, 0),
                LocalTime.of(18, 0)
        );

        final var payload = objectMapper.writeValueAsString(createEventRequest);
        final var request = post("/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload);

        mockMvc.perform(request)
                .andExpect(status().isCreated());

        final var responseBody = mockMvc.perform(request)
                .andExpect(status().isConflict())
                .andReturn()
                .getResponse()
                .getContentAsString();

        final var response = objectMapper.readValue(responseBody, StandardErrorResponse.class);

        final var violationCode = ViolationCode.of(response.code());
        Assertions.assertTrue(violationCode.isPresent());
        Assertions.assertEquals(ViolationCode.CONFLICT_PRETTY_NAME_ALREADY_EXISTS, violationCode.get());
        Assertions.assertNotNull(response.message());
        Assertions.assertFalse(response.message().isBlank());
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

        final var error = objectMapper.readValue(responseBody, StandardErrorResponse.class);

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
        final var beforeYesterday = OffsetDateTime.now().minusDays(2).toLocalDate();
        final var yesterday = OffsetDateTime.now().minusDays(1).toLocalDate();
        final var event = new CreateEventRequest(
                "",
                "",
                BigDecimal.valueOf(-100.00),
                yesterday,
                beforeYesterday,
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

        final var error = objectMapper.readValue(responseBody, StandardErrorResponse.class);

        assertEquals("invalid-payload", error.code());
        assertTrue(OffsetDateTime.now().isAfter(error.moment()));
        assertFalse(error.message().isBlank());
        assertFalse(error.issues().isEmpty());
        List.of("name", "location", "price", "startDate", "endDate")
                .forEach((fieldName) -> assertTrue(error.issues().containsKey(fieldName)));
    }
}
