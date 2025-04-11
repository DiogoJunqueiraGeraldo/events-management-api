package com.isiflix.events_management_api.integration.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isiflix.events_management_api.app.events.dtos.CreateEventDTO;
import com.isiflix.events_management_api.app.events.dtos.EventDTO;
import com.isiflix.events_management_api.app.shared.PaginationResponse;
import com.isiflix.events_management_api.infra.database.event.JPAEventRepository;
import com.isiflix.events_management_api.utils.PostgresTestContainerConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.core.type.TypeReference;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.concurrent.StructuredTaskScope;
import java.util.stream.IntStream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(PostgresTestContainerConfiguration.class)
public class ListEventIntegrationTest {
    private static final int SETUP_DATESET_SIZE = 1000;
    private static final long SETUP_DEADLINE_IN_SECONDS = 2;

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @Autowired
    public ListEventIntegrationTest(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    private static CreateEventDTO createEventMock(int i) {
        final var morningTime = LocalTime.of(10, 0);
        final var eveningTime = LocalTime.of(20, 0);

        final var tomorrow = LocalDate.now().plusDays(i);

        return new CreateEventDTO(
                "Event %d".formatted(i),
                "Location %d".formatted(i),
                BigDecimal.valueOf(i * 10L),
                tomorrow.atTime(morningTime),
                tomorrow.atTime(eveningTime)
        );
    }

    @BeforeAll
    public static void setUp(
            @Autowired MockMvc mockMvc,
            @Autowired ObjectMapper objectMapper,
            @Autowired JPAEventRepository jpaEventRepository
    ) throws Exception {
        jpaEventRepository.deleteAll();
        try (var scope = new StructuredTaskScope<Void>()) {
            IntStream.rangeClosed(1, SETUP_DATESET_SIZE)
                    .forEach(i -> scope.fork(() -> {
                        final var createEventRequest = createEventMock(i);
                        final var payload = objectMapper.writeValueAsString(createEventRequest);
                        final var request = post("/events")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(payload);

                        mockMvc.perform(request).andExpect(status().isCreated());
                        return null;
                    }));

            final var deadline = Instant.now().plusSeconds(SETUP_DEADLINE_IN_SECONDS);
            scope.joinUntil(deadline);
        }
    }

    @Test
    @DisplayName("Integration Test - Default pagination")
    public void shouldHaveDefaultPaginationParameters() throws Exception {
        final var responseBody = mockMvc.perform(get("/events"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        final PaginationResponse<EventDTO> paginationResult = objectMapper
                .readValue(responseBody, new TypeReference<>() {
                });
        Assertions.assertEquals(1, paginationResult.pagination().page());
        Assertions.assertEquals(paginationResult.items().size(), paginationResult.pagination().size());
    }
}
