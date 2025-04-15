package com.isiflix.events_management_api.integration.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isiflix.events_management_api.app.errors.StandardErrorResponse;
import com.isiflix.events_management_api.app.events.rest.CreateEventRequest;
import com.isiflix.events_management_api.app.events.rest.EventResponse;
import com.isiflix.events_management_api.domain.errors.ViolationCode;
import com.isiflix.events_management_api.infra.database.events.JPAEventRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class CreateEventIntegrationTest {
    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    JPAEventRepository jpaEventRepository;

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

        assertThat(response.id()).isNotNull();
        assertThat(response.name()).isEqualTo(createEventRequest.name());
        assertThat(response.price()).isEqualTo(createEventRequest.price());
        assertThat(response.location()).isEqualTo(createEventRequest.location());
        assertThat(response.startDate()).isEqualTo(createEventRequest.startDate());
        assertThat(response.endDate()).isEqualTo(createEventRequest.endDate());
        assertThat(response.startTime()).isEqualTo(createEventRequest.startTime());
        assertThat(response.endTime()).isEqualTo(createEventRequest.endTime());
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

        assertThat(violationCode).isPresent();
        assertThat(violationCode.get()).isEqualTo(ViolationCode.CONFLICT_PRETTY_NAME_ALREADY_EXISTS);
        assertThat(response.message()).isNotBlank();
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

        assertThat(error.code()).isEqualTo("invalid-payload");
        assertThat(OffsetDateTime.now()).isAfter(error.moment());
        assertThat(error.message()).isNotBlank();
        assertThat(error.issues()).containsOnlyKeys(
                "name","location", "price", "startDate",
                "endDate", "startTime", "endTime");
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

        assertThat(error.code()).isEqualTo("invalid-payload");
        assertThat(OffsetDateTime.now()).isAfter(error.moment());
        assertThat(error.message()).isNotBlank();
        assertThat(error.issues()).containsOnlyKeys("name", "location", "price", "startDate", "endDate");
    }
}
