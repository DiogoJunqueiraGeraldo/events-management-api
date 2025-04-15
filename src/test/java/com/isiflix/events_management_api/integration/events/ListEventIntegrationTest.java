package com.isiflix.events_management_api.integration.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isiflix.events_management_api.app.events.rest.EventPaginationResponse;
import com.isiflix.events_management_api.infra.database.events.EventEntity;
import com.isiflix.events_management_api.infra.database.events.JPAEventRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class ListEventIntegrationTest {
    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    private static final int SETUP_DATESET_SIZE = 1000;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    JPAEventRepository jpaEventRepository;

    private static EventEntity createEventMock(int i) {
        final var morningTime = LocalTime.of(10, 0);
        final var eveningTime = LocalTime.of(20, 0);

        final var tomorrow = LocalDate.now().plusDays(i);


        return new EventEntity(
                null,
                "Event %d".formatted(i),
                "event-%d".formatted(i),
                "Location %d".formatted(i),
                BigDecimal.valueOf(i),
                tomorrow.atTime(morningTime),
                tomorrow.atTime(eveningTime)
        );
    }

    public static boolean eventsLoaded = false;
    @BeforeEach
    void setUp() {
        if(!eventsLoaded) {
            List<EventEntity> events = IntStream.range(0, SETUP_DATESET_SIZE)
                    .mapToObj(ListEventIntegrationTest::createEventMock)
                    .toList();

            jpaEventRepository.saveAllAndFlush(events);
        }
        eventsLoaded = true;
    }

    @Test
    @DisplayName("Integration Test - Default pagination")
    public void shouldHaveDefaultPaginationParameters() throws Exception {
        final var responseBody = mockMvc.perform(get("/events"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        final EventPaginationResponse paginationResult = objectMapper
                .readValue(responseBody, EventPaginationResponse.class);

        assertThat(paginationResult.pagination().page()).isEqualTo(1);
        assertThat(paginationResult.items().size()).isEqualTo(paginationResult.pagination().size());
    }

    @Test
    @DisplayName("Integration Test - Next Page")
    public void shouldHaveNextPage() throws Exception {
        final int halfDataset = SETUP_DATESET_SIZE / 2;
        final var request = get("/events")
                .queryParam("page", "2")
                .queryParam("size", Integer.toString(halfDataset));

        final var responseBody = mockMvc.perform(request)
                .andReturn()
                .getResponse()
                .getContentAsString();

        final EventPaginationResponse paginationResult = objectMapper
                .readValue(responseBody, EventPaginationResponse.class);

        assertThat(paginationResult.pagination().page()).isEqualTo(2);
        assertThat(paginationResult.items().size()).isEqualTo(paginationResult.pagination().size());
        assertThat(paginationResult.items().size()).isEqualTo(halfDataset);
    }

    @Test
    @DisplayName("Integration Test - Last Page")
    public void shouldHaveLastPage() throws Exception {
        final int pageSize = SETUP_DATESET_SIZE / 2;
        final var request = get("/events")
                .queryParam("page", "3")
                .queryParam("size", Integer.toString(pageSize));

        final var responseBody = mockMvc.perform(request)
                .andReturn()
                .getResponse()
                .getContentAsString();

        final EventPaginationResponse paginationResult = objectMapper
                .readValue(responseBody, EventPaginationResponse.class);

        assertThat(paginationResult.pagination().page()).isEqualTo(3);
        assertThat(paginationResult.items().size()).isEqualTo(paginationResult.pagination().size());
        assertThat(paginationResult.items().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("Integration Test - Shouldn't Overlap Items")
    public void shouldNotOverlapItems() throws Exception {
        Set<Long> ids = new HashSet<>(SETUP_DATESET_SIZE);
        final int pageSize = SETUP_DATESET_SIZE / 7;

        final int UPPER_LIMIT = (SETUP_DATESET_SIZE / pageSize) + 1;
        for(int i = 1; i < UPPER_LIMIT; i++) {
            final var request = get("/events")
                    .queryParam("page", Integer.toString(i))
                    .queryParam("size", Integer.toString(pageSize));

            final var responseBody = mockMvc.perform(request)
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            final EventPaginationResponse paginationResult = objectMapper
                    .readValue(responseBody, EventPaginationResponse.class);


            assertThat(paginationResult.items()).allSatisfy(event -> {
                assertThat(ids).doesNotContain(event.id());
                ids.add(event.id());
            });

            if(paginationResult.items().size() < pageSize) {
                break;
            }
        }
    }
}
