package com.isiflix.events_management_api.integration.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isiflix.events_management_api.app.errors.StandardErrorResponse;
import com.isiflix.events_management_api.app.events.rest.CreateEventRequest;
import com.isiflix.events_management_api.app.events.rest.EventResponse;
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
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class FindEventIntegrationTest {
    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    JPAEventRepository jpaEventRepository;

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    @BeforeEach
    void setUp() {
        this.jpaEventRepository.deleteAll();
    }

    @Test
    @DisplayName("Integration Test - Find by pretty name")
    public void shouldFindByPrettyName() throws Exception {
        final var tomorrow = LocalDate.now().plusDays(1);
        final var startTime = LocalTime.of(8, 0);
        final var endTime = LocalTime.of(18, 0);
        final var forFree = BigDecimal.ZERO;

        final String expectedPrettyName = "isiflix-live-chatting";
        final var createEventRequest = new CreateEventRequest(
                "IsiFlix Live Chatting!",
                "online",
                forFree,
                tomorrow,
                tomorrow,
                startTime,
                endTime
        );

        final var payload = objectMapper.writeValueAsString(createEventRequest);
        final var request = post("/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload);

        mockMvc.perform(request).andExpect(status().isCreated());

        final var responseBody = mockMvc
                .perform(get("/events/{prettyName}", expectedPrettyName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.startDate").value(dateFormatter.format(tomorrow)))
                .andExpect(jsonPath("$.endDate").value(dateFormatter.format(tomorrow)))
                .andExpect(jsonPath("$.startTime").value(timeFormatter.format(startTime)))
                .andExpect(jsonPath("$.endTime").value(timeFormatter.format(endTime)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        final var response = objectMapper.readValue(responseBody, EventResponse.class);

        assertThat(response.id()).isNotNull();
        assertThat(response.name()).isEqualTo(createEventRequest.name());
        assertThat(response.prettyName()).isEqualTo(expectedPrettyName);
        assertThat(response.price()).isEqualByComparingTo(createEventRequest.price());
        assertThat(response.location()).isEqualTo(createEventRequest.location());
        assertThat(response.startDate()).isEqualTo(createEventRequest.startDate());
        assertThat(response.endDate()).isEqualTo(createEventRequest.endDate());
        assertThat(response.startTime()).isEqualTo(createEventRequest.startTime());
        assertThat(response.endTime()).isEqualTo(createEventRequest.endTime());
    }

    @Test
    @DisplayName("Integration Test - Not Found - Search event that doesnt exist")
    public void shouldReturnNotFound() throws Exception {
        final var responseBody = mockMvc
                .perform(get("/events/{prettyName}", "foo-bar"))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();

        final var response = objectMapper.readValue(responseBody, StandardErrorResponse.class);
        assertThat(response.code()).isEqualTo("not-found");
        assertThat(response.message()).isNotBlank();
    }
}
