package com.isiflix.events_management_api.integration.subscriptions;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.isiflix.events_management_api.app.subscriptions.rest.responses.ReferralPodiumItemResponse;
import com.isiflix.events_management_api.infra.database.events.EventEntity;
import com.isiflix.events_management_api.infra.database.events.JPAEventRepository;
import com.isiflix.events_management_api.infra.database.subscriptions.JPASubscriptionRepository;
import com.isiflix.events_management_api.infra.database.subscriptions.SubscriptionEntity;
import com.isiflix.events_management_api.infra.database.users.JPAUserRepository;
import com.isiflix.events_management_api.infra.database.users.UserEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
public class CalculatePodiumIntegrationTest {
    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    JPAEventRepository jpaEventRepository;

    @Autowired
    JPAUserRepository jpaUserRepository;

    @Autowired
    JPASubscriptionRepository jpaSubscriptionRepository;

    private static final String EVENT_PRETTY_NAME = "isi-mock-event";

    @BeforeEach
    void setUp() {
        final var mockEvent = new EventEntity(null, "Isi Mock Event!", EVENT_PRETTY_NAME, "online",
                BigDecimal.ONE, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2));


        final var referrer1 = new UserEntity(null, "First", "first@referral.com");
        final var referrer2 = new UserEntity(null, "Second", "second@referral.com");
        final var referrer3 = new UserEntity(null, "Third", "third@referral.com");
        final var referrer4 = new UserEntity(null, "Forth", "forth@referral.com");

        List<UserEntity> mockUsers1 = IntStream.range(0, 10)
                .mapToObj(i -> new UserEntity(null, "Isi Mock User " + i, i + "@mock.com"))
                .toList();

        List<UserEntity> mockUsers2 = IntStream.range(10, 15)
                .mapToObj(i -> new UserEntity(null, "Isi Mock User " + i, i + "@mock.com"))
                .toList();

        List<UserEntity> mockUsers3 = IntStream.range(15, 18)
                .mapToObj(i -> new UserEntity(null, "Isi Mock User " + i, i + "@mock.com"))
                .toList();

        List<UserEntity> mockUsers4 = IntStream.range(18, 20)
                .mapToObj(i -> new UserEntity(null, "Isi Mock User " + i, i + "@mock.com"))
                .toList();


        List<UserEntity> allUsers = new ArrayList<>(List.of(referrer1, referrer2, referrer3, referrer4));
        allUsers.addAll(mockUsers1);
        allUsers.addAll(mockUsers2);
        allUsers.addAll(mockUsers3);
        allUsers.addAll(mockUsers4);


        final var mockSubscriptions1 = mockUsers1.stream().map(user -> new SubscriptionEntity(null, mockEvent, user, referrer1)).toList();
        final var mockSubscriptions2 = mockUsers2.stream().map(user -> new SubscriptionEntity(null, mockEvent, user, referrer2)).toList();
        final var mockSubscriptions3 = mockUsers3.stream().map(user -> new SubscriptionEntity(null, mockEvent, user, referrer3)).toList();
        final var mockSubscriptions4 = mockUsers4.stream().map(user -> new SubscriptionEntity(null, mockEvent, user, referrer4)).toList();

        List<SubscriptionEntity> allSubscriptions = new ArrayList<>();
        allSubscriptions.addAll(mockSubscriptions1);
        allSubscriptions.addAll(mockSubscriptions2);
        allSubscriptions.addAll(mockSubscriptions3);
        allSubscriptions.addAll(mockSubscriptions4);

        jpaEventRepository.saveAndFlush(mockEvent);
        jpaUserRepository.saveAllAndFlush(allUsers);
        jpaSubscriptionRepository.saveAllAndFlush(allSubscriptions);
    }

    @AfterEach
    void tearDown() {
        jpaSubscriptionRepository.deleteAll();
        jpaUserRepository.deleteAll();
        jpaEventRepository.deleteAll();
    }

    @Test
    @DisplayName("Integration Test - Query Podium Ranking")
    public void shouldReturnThreeTopReferralUsersForEvent() throws Exception {
        final var request = get("/subscriptions/{prettyName}/ranking", EVENT_PRETTY_NAME);
        final var responseBody = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        final var response = objectMapper.readValue(responseBody, new TypeReference<List<ReferralPodiumItemResponse>>() {});

        assertThat(response.size()).isEqualTo(3);
        assertThat(response.getFirst().userName()).isEqualTo("First");
        assertThat(response.getFirst().subscribers()).isEqualTo(10);

        assertThat(response.get(1).userName()).isEqualTo("Second");
        assertThat(response.get(1).subscribers()).isEqualTo(5);

        assertThat(response.getLast().userName()).isEqualTo("Third");
        assertThat(response.getLast().subscribers()).isEqualTo(3);
    }

    @Test
    @DisplayName("Integration Test - Podium of Non Existing Event")
    public void shouldReturnNotFoundForNonExistingEvent() throws Exception {
        final var request = get("/subscriptions/{prettyName}/ranking", "non-existing");
        mockMvc.perform(request).andExpect(status().isNotFound());
    }
}
