package com.isiflix.events_management_api.integration.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isiflix.events_management_api.app.errors.StandardErrorResponse;
import com.isiflix.events_management_api.app.events.rest.CreateEventRequest;
import com.isiflix.events_management_api.app.events.rest.EventResponse;
import com.isiflix.events_management_api.infra.database.event.JPAEventRepository;
import com.isiflix.events_management_api.utils.PostgresTestContainerConfiguration;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(PostgresTestContainerConfiguration.class)
public class FindEventIntegrationTest {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final JPAEventRepository jpaEventRepository;

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Autowired
    public FindEventIntegrationTest(MockMvc mockMvc, ObjectMapper objectMapper, JPAEventRepository jpaEventRepository) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.jpaEventRepository = jpaEventRepository;
    }

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

        Assertions.assertNotNull(response.id());
        Assertions.assertEquals(createEventRequest.name(), response.name());
        Assertions.assertEquals(expectedPrettyName, response.prettyName());
        Assertions.assertEquals(createEventRequest.price().doubleValue(), response.price().doubleValue());
        Assertions.assertEquals(createEventRequest.location(), response.location());
        Assertions.assertEquals(createEventRequest.startDate(), response.startDate());
        Assertions.assertEquals(createEventRequest.endDate(), response.endDate());
        Assertions.assertEquals(createEventRequest.startTime(), response.startTime());
        Assertions.assertEquals(createEventRequest.endTime(), response.endTime());
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
        Assertions.assertEquals("not-found", response.code());
        Assertions.assertNotNull(response.message());
        Assertions.assertFalse(response.message().isBlank());
    }
}
