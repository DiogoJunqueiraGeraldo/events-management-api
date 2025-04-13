package com.isiflix.events_management_api.integration.subscriptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isiflix.events_management_api.app.errors.StandardErrorResponse;
import com.isiflix.events_management_api.app.subscriptions.rest.CreateSubscriptionRequest;
import com.isiflix.events_management_api.domain.errors.ViolationCode;
import com.isiflix.events_management_api.utils.PostgresTestContainerConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(PostgresTestContainerConfiguration.class)
public class CreateSubscriptionIntegrationTest {
    private static final String NON_EXISTING_EVENT = "isiflix-live-coding";
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @Autowired
    public CreateSubscriptionIntegrationTest(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @Test
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
}
