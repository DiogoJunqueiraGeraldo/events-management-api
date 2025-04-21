package com.isiflix.events_management_api.domain.events;

import com.isiflix.events_management_api.app.events.dtos.CreateEventDTO;
import com.isiflix.events_management_api.domain.errors.BusinessRuleViolationException;
import com.isiflix.events_management_api.domain.errors.ViolationCode;
import com.isiflix.events_management_api.domain.events.vos.EventPeriodTest;
import com.isiflix.events_management_api.domain.users.UserFactory;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SubscriptionTest {

    @Test
    public void shouldCreateNewSubscriptionWithoutReferrer() {
        final var referenceDateTime = LocalDateTime.now();
        final var event = EventFactory.create(
                new CreateEventDTO(
                        "Foo Bar",
                        "online",
                        BigDecimal.ZERO,
                        referenceDateTime,
                        referenceDateTime.plusMinutes(EventPeriodTest.VALID_EVENT_DURATION_IN_MINUTES)
                )
        );

        final var user = UserFactory.fromRaw(1L, "barry allen", "barry@allen.com");
        final var subscription = new Subscription(null, event, user, null);

        assertThat(subscription).isNotNull();
        assertThat(subscription.id()).isNull();
        assertThat(subscription.event()).isEqualTo(event);
        assertThat(subscription.user()).isEqualTo(user);
        assertThat(subscription.referrer()).isNull();
    }

    @Test
    public void shouldCreateNewSubscriptionWithReferrer() {
        final var referenceDateTime = LocalDateTime.now();
        final var event = EventFactory.create(
                new CreateEventDTO(
                        "Foo Bar",
                        "online",
                        BigDecimal.ZERO,
                        referenceDateTime,
                        referenceDateTime.plusMinutes(EventPeriodTest.VALID_EVENT_DURATION_IN_MINUTES)
                )
        );

        final var user = UserFactory.fromRaw(1L, "barry allen", "barry@allen.com");
        final var referrer = UserFactory.fromRaw(2L, "mary jane", "mary@jane.com");
        final var subscription = new Subscription(null, event, user, referrer);

        assertThat(subscription).isNotNull();
        assertThat(subscription.id()).isNull();
        assertThat(subscription.event()).isEqualTo(event);
        assertThat(subscription.user()).isEqualTo(user);
        assertThat(subscription.referrer()).isEqualTo(referrer);
    }

    @Test
    public void shouldRequireEvent() {
        final var user = UserFactory.fromRaw(1L, "barry allen", "barry@allen.com");
        final var referrer = UserFactory.fromRaw(2L, "mary jane", "mary@jane.com");

        assertThatThrownBy(() -> new Subscription(null, null, user, referrer))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Subscription 'event' cannot be null");
    }

    @Test
    public void shouldRequireUser() {
        final var referenceDateTime = LocalDateTime.now();
        final var event = EventFactory.create(
                new CreateEventDTO(
                        "Foo Bar",
                        "online",
                        BigDecimal.ZERO,
                        referenceDateTime,
                        referenceDateTime.plusMinutes(EventPeriodTest.VALID_EVENT_DURATION_IN_MINUTES)
                )
        );
        final var referrer = UserFactory.fromRaw(2L, "mary jane", "mary@jane.com");

        assertThatThrownBy(() -> new Subscription(null, event, null, referrer))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Subscription 'user' cannot be null");
    }

    @Test
    public void shouldNotAllowSelfReferral() {
        final var referenceDateTime = LocalDateTime.now();
        final var event = EventFactory.create(
                new CreateEventDTO(
                        "Foo Bar",
                        "online",
                        BigDecimal.ZERO,
                        referenceDateTime,
                        referenceDateTime.plusMinutes(EventPeriodTest.VALID_EVENT_DURATION_IN_MINUTES)
                )
        );
        final var referrer = UserFactory.fromRaw(2L, "mary jane", "mary@jane.com");

        final var businessException = (BusinessRuleViolationException) assertThatThrownBy(() -> new Subscription(null, event, referrer, referrer))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessage("Subscription 'referrer' cannot be the same user")
                .actual();

        assertThat(businessException.getViolationCode()).isEqualTo(ViolationCode.CONFLICT_CANT_REFER_ITSELF);
        assertThat(businessException.getMetadata()).containsEntry("userId", referrer.getId());
    }

    @Test
    public void shouldBuildDTO() {
        final var referenceDateTime = LocalDateTime.now();
        final var event = EventFactory.create(
                new CreateEventDTO(
                        "Foo Bar",
                        "online",
                        BigDecimal.ZERO,
                        referenceDateTime,
                        referenceDateTime.plusMinutes(EventPeriodTest.VALID_EVENT_DURATION_IN_MINUTES)
                )
        );

        final var user = UserFactory.fromRaw(1L, "barry allen", "barry@allen.com");
        final var referrer = UserFactory.fromRaw(2L, "mary jane", "mary@jane.com");
        final var subscription = new Subscription(1L, event, user, referrer);

        final var subscriptionDTO = subscription.toDTO();
        assertThat(subscriptionDTO).isNotNull();
        assertThat(subscriptionDTO.id()).isEqualTo(1L);
        assertThat(subscriptionDTO.eventDTO()).isEqualTo(event.toDTO());
        assertThat(subscriptionDTO.userDTO()).isEqualTo(user.toDTO());
    }
}
