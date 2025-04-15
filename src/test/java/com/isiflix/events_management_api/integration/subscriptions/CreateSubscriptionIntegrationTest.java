package com.isiflix.events_management_api.integration.subscriptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isiflix.events_management_api.app.errors.StandardErrorResponse;
import com.isiflix.events_management_api.app.subscriptions.rest.CreateSubscriptionRequest;
import com.isiflix.events_management_api.app.subscriptions.rest.CreateSubscriptionResponse;
import com.isiflix.events_management_api.domain.errors.ViolationCode;
import com.isiflix.events_management_api.infra.database.events.EventEntity;
import com.isiflix.events_management_api.infra.database.events.JPAEventRepository;
import com.isiflix.events_management_api.infra.database.subscriptions.JPASubscriptionRepository;
import com.isiflix.events_management_api.infra.database.users.JPAUserRepository;
import com.isiflix.events_management_api.infra.database.users.UserEntity;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CreateSubscriptionIntegrationTest {
    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    private static final String NON_EXISTING_EVENT = "non-existing-event";
    private static final String EXISTING_EVENT = "isiflix-live-coding";

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    JPAEventRepository jpaEventRepository;

    @Autowired
    JPASubscriptionRepository jpaSubscriptionRepository;

    @Autowired
    JPAUserRepository jpaUserRepository;

    private static boolean eventLoaded = false;
    @BeforeEach
    void setUp() {
        if(!eventLoaded) {
            EventEntity eventEntity = new EventEntity(
                    null,
                    "Isiflix Live Coding!",
                    EXISTING_EVENT,
                    "online",
                    BigDecimal.ONE,
                    LocalDateTime.now().plusHours(1),
                    LocalDateTime.now().plusHours(2)
            );

            jpaEventRepository.save(eventEntity);

            UserEntity userEntity = new UserEntity(
                    null,
                    "Foo Bar",
                    "foo@bar.com"
            );

            jpaUserRepository.save(userEntity);
        }

        eventLoaded = true;
    }

    @AfterEach
    void tearDown() {
        jpaSubscriptionRepository.deleteAll();
    }

    @Test
    @DisplayName("Integraiton Test - Required Fields")
    public void shouldReturnBadRequestWhenUserNameAndEmailIsNotProvided() throws Exception {
        final var createSubscriptionRequest = new CreateSubscriptionRequest(null, null);
        final var requestBody = objectMapper.writeValueAsString(createSubscriptionRequest);
        final var request = post("/subscriptions/{prettyName}", NON_EXISTING_EVENT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody);

        final var responseBody = mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        final var error = objectMapper.readValue(responseBody, StandardErrorResponse.class);
        Assertions.assertTrue(error.issues().containsKey("userName"));
        Assertions.assertTrue(error.issues().containsKey("email"));
    }

    @Test
    @DisplayName("Integration Test - Required User Name Field")
    public void shouldReturnBadRequestWhenUserNameIsNotProvided() throws Exception {
        final var createSubscriptionRequest = new CreateSubscriptionRequest(null, "john@doe.com");
        final var requestBody = objectMapper.writeValueAsString(createSubscriptionRequest);
        final var request = post("/subscriptions/{prettyName}", NON_EXISTING_EVENT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody);

        final var responseBody = mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        final var error = objectMapper.readValue(responseBody, StandardErrorResponse.class);
        Assertions.assertTrue(error.issues().containsKey("userName"));
        Assertions.assertFalse(error.issues().containsKey("email"));
    }

    @Test
    @DisplayName("Integration Test - Required Email Field")
    public void shouldReturnBadRequestWhenEmailIsNotProvided() throws Exception {
        final var createSubscriptionRequest = new CreateSubscriptionRequest("John Doe", null);
        final var requestBody = objectMapper.writeValueAsString(createSubscriptionRequest);
        final var request = post("/subscriptions/{prettyName}", NON_EXISTING_EVENT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody);

        final var responseBody = mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        final var error = objectMapper.readValue(responseBody, StandardErrorResponse.class);
        Assertions.assertFalse(error.issues().containsKey("userName"));
        Assertions.assertTrue(error.issues().containsKey("email"));
    }

    @Test
    @DisplayName("Integration Test - Reject When Event Not Found")
    public void shouldReturnNotFoundForEventNotFound() throws Exception {
        final var createSubscriptionRequest = new CreateSubscriptionRequest("John Doe", "john@doe.com");
        final var requestBody = objectMapper.writeValueAsString(createSubscriptionRequest);
        final var request = post("/subscriptions/{prettyName}", NON_EXISTING_EVENT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody);

        final var responseBody = mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();

        final var error = objectMapper.readValue(responseBody, StandardErrorResponse.class);
        Assertions.assertEquals(ViolationCode.CANT_SUBSCRIBE_TO_NON_EXISTING_EVENT.toString(), error.code());
        Assertions.assertTrue(error.message().contains(NON_EXISTING_EVENT));
    }



    @Test
    @DisplayName("Integration Test - Create Subscription For Non Existing User")
    public void shouldReturnSubscriptionNumberAndBuildDesignationForNewUser() throws Exception {
        jpaUserRepository.findByEmail("john@doe.com")
                .ifPresent(userEntity -> jpaUserRepository.delete(userEntity));

        final var createSubscriptionRequest = new CreateSubscriptionRequest("John Doe", "john@doe.com");
        final var requestBody = objectMapper.writeValueAsString(createSubscriptionRequest);
        final var request = post("/subscriptions/{prettyName}", EXISTING_EVENT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody);

        final var responseBody = mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        final var response = objectMapper.readValue(responseBody, CreateSubscriptionResponse.class);

        final var subscriptionEntity = jpaSubscriptionRepository.findById(response.subscriptionId());
        assertThat(subscriptionEntity).isPresent();
        assertThat(response.subscriptionId()).isEqualTo(subscriptionEntity.get().getId());

        final var user = jpaUserRepository.findByEmail("john@doe.com");
        assertThat(user).isPresent();

        final var expectedDesignation = "/%s/%d".formatted(EXISTING_EVENT, user.get().getId());
        assertThat(response.designation()).endsWith(expectedDesignation);
    }

    @Test
    @DisplayName("Integration Test - Create Subscription For Existing User")
    public void shouldReturnSubscriptionNumberAndBuildDesignationForExistingUser() throws Exception {
        final var existingUser = jpaUserRepository.findByEmail("foo@bar.com");
        assertThat(existingUser).isPresent();

        final var createSubscriptionRequest = new CreateSubscriptionRequest("Foo Bar", "foo@bar.com");
        final var requestBody = objectMapper.writeValueAsString(createSubscriptionRequest);
        final var request = post("/subscriptions/{prettyName}", EXISTING_EVENT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody);

        final var responseBody = mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        final var response = objectMapper.readValue(responseBody, CreateSubscriptionResponse.class);

        final var subscriptionEntity = jpaSubscriptionRepository.findById(response.subscriptionId());
        assertThat(subscriptionEntity).isPresent();
        assertThat(response.subscriptionId()).isEqualTo(subscriptionEntity.get().getId());
        assertThat(response.designation()).endsWith(existingUser.get().getId().toString());
    }

    @Test
    @DisplayName("Integration Test - Conflicting Subscription")
    public void shouldReturnConflictForExistingSubscription() throws Exception {
        final var createSubscriptionRequest = new CreateSubscriptionRequest("Foo Bar", "foo@bar.com");
        final var requestBody = objectMapper.writeValueAsString(createSubscriptionRequest);
        final var request = post("/subscriptions/{prettyName}", EXISTING_EVENT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody);

        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        mockMvc.perform(request).andExpect(status().isConflict());
    }
}
