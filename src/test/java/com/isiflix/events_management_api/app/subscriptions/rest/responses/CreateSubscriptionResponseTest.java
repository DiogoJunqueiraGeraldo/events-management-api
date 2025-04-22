package com.isiflix.events_management_api.app.subscriptions.rest.responses;

import com.isiflix.events_management_api.app.events.dtos.EventDTO;
import com.isiflix.events_management_api.app.subscriptions.dtos.SubscriptionDTO;
import com.isiflix.events_management_api.app.users.dtos.UserDTO;
import com.isiflix.events_management_api.domain.events.vos.EventPeriodTest;
import org.h2.engine.User;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CreateSubscriptionResponseTest {
    @Test
    public void shouldBuildDesignation() {
        final var referenceDateTime = LocalDateTime.now();

        final var eventDTO = new EventDTO(
                2L, "Foo Bar", "foo-bar", "location",
                BigDecimal.ZERO, referenceDateTime, referenceDateTime.plusMinutes(EventPeriodTest.VALID_EVENT_DURATION_IN_MINUTES)
        );

        final var userDTO = new UserDTO(3L, "John Doe", "john@doe.com");

        final var createSubscriptionResponse = CreateSubscriptionResponse.fromDTO(
                new SubscriptionDTO(1L, eventDTO, userDTO),
                "https://example.com"
        );

        assertThat(createSubscriptionResponse.subscriptionId()).isEqualTo(1L);
        assertThat(createSubscriptionResponse.designation())
                .isEqualTo("https://example.com/subscriptions/%s/%d"
                        .formatted(
                                eventDTO.prettyName(),
                                userDTO.id()
                        )
                );
    }
}