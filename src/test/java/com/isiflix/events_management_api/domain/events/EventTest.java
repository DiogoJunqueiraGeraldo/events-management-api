package com.isiflix.events_management_api.domain.events;

import com.isiflix.events_management_api.app.events.dtos.EventDTO;
import com.isiflix.events_management_api.domain.events.vos.EventPeriod;
import com.isiflix.events_management_api.domain.events.vos.EventPeriodTest;
import com.isiflix.events_management_api.domain.events.vos.EventPrettyName;
import com.isiflix.events_management_api.domain.users.UserFactory;
import lombok.Builder;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class EventTest {
    private static final LocalDateTime referenceDatetime = LocalDateTime.now();

    @Builder
    static class EventBuilder {
        @Builder.Default
        private Long id = 1L;

        @Builder.Default
        private String name = "Foo Bar";

        @Builder.Default
        private EventPrettyName prettyName = new EventPrettyName("foo-bar");

        @Builder.Default
        private String location = "online";

        @Builder.Default
        private BigDecimal price = BigDecimal.ZERO;

        @Builder.Default
        private EventPeriod period =
                new EventPeriod(
                        referenceDatetime,
                        referenceDatetime.plusMinutes(EventPeriodTest.VALID_EVENT_DURATION_IN_MINUTES));

        public Event newEvent() {
            return new Event(name, location, price, period);
        }

        public Event deserialize() {
            return new Event(id, name, prettyName, location, price, period);
        }
    }

    @Test
    public void shouldCreateNewEventInstance() {
        final var event = EventBuilder.builder().build().newEvent();
        final var dto = new EventDTO(
                null,
                "Foo Bar",
                "foo-bar",
                "online",
                BigDecimal.ZERO,
                referenceDatetime,
                referenceDatetime.plusMinutes(EventPeriodTest.VALID_EVENT_DURATION_IN_MINUTES)
        );

        assertThat(event.toDTO()).isEqualTo(dto);
    }

    @Test
    public void shouldDeserializeEventInstance() {
        final var event = EventBuilder.builder().build().deserialize();
        final var dto = new EventDTO(
                1L,
                "Foo Bar",
                "foo-bar",
                "online",
                BigDecimal.ZERO,
                referenceDatetime,
                referenceDatetime.plusMinutes(EventPeriodTest.VALID_EVENT_DURATION_IN_MINUTES)
        );

        assertThat(event.toDTO()).isEqualTo(dto);
    }

    @Test
    public void shouldSubscribeUserWithoutReferrer() {
        final var event = EventBuilder.builder().build().newEvent();
        final var user = UserFactory.fromRaw(1L, "john doe", "john@doe.com");

        final var subscription = event.subscribe(user, null);
        assertThat(subscription).isNotNull();
        assertThat(subscription.id()).isNull();
        assertThat(subscription.referrer()).isNull();
        assertThat(subscription.event()).isEqualTo(event);
        assertThat(subscription.user()).isEqualTo(user);
    }

    @Test
    public void shouldSubscribeUserWithReferrer() {
        final var event = EventBuilder.builder().build().newEvent();
        final var user = UserFactory.fromRaw(1L, "john doe", "john@doe.com");
        final var referrer = UserFactory.fromRaw(2L, "barry allen", "barry@allen.com");

        final var subscription = event.subscribe(user, referrer);

        assertThat(subscription).isNotNull();
        assertThat(subscription.id()).isNull();
        assertThat(subscription.referrer()).isEqualTo(referrer);
        assertThat(subscription.event()).isEqualTo(event);
        assertThat(subscription.user()).isEqualTo(user);
    }

    @Test
    public void shouldRequireName() {
        String[] invalidEventNames = {null, "", " "};
        Consumer<String> throwForInvalidName =
                invalidName ->
                        assertThatThrownBy(() -> EventBuilder.builder().name(invalidName).build().newEvent())
                                .isInstanceOf(IllegalArgumentException.class)
                                .hasMessage("Event 'name' should not be null or blank");

        assertThat(invalidEventNames).allSatisfy(throwForInvalidName);
    }

    @Test
    public void shouldRequireLocation() {
        String[] invalidEventLocations = {null, "", " "};
        Consumer<String> throwForInvalidLocation =
                invalidLocation ->
                        assertThatThrownBy(() -> EventBuilder.builder().location(invalidLocation).build().newEvent())
                                .isInstanceOf(IllegalArgumentException.class)
                                .hasMessage("Event 'location' should not be null or blank");

        assertThat(invalidEventLocations).allSatisfy(throwForInvalidLocation);
    }

    @Test
    public void shouldRequirePrice() {
        BigDecimal[] invalidEventPrices = {null, BigDecimal.valueOf(-1)};
        Consumer<BigDecimal> throwForInvalidPrice =
                invalidPrice ->
                        assertThatThrownBy(() -> EventBuilder.builder().price(invalidPrice).build().newEvent())
                                .isInstanceOf(IllegalArgumentException.class)
                                .hasMessage("Event 'price' should not be negative or null");

        assertThat(invalidEventPrices).allSatisfy(throwForInvalidPrice);
    }

  @Test
  public void shouldRequirePrettyName() {
    assertThatThrownBy(() -> EventBuilder.builder().prettyName(null).build().deserialize())
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Event 'prettyName' should not be null");
  }

    @Test
    public void shouldRequirePeriod() {
        assertThatThrownBy(() -> EventBuilder.builder().period(null).build().newEvent())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Event 'period' should not be null");
    }

    @Test
    public void shouldRequireId() {
        Long[] invalidEventIds = {null, -1L};

        Consumer<Long> throwForInvalidId = invalidId -> assertThatThrownBy(() -> EventBuilder.builder().id(invalidId).build().deserialize())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Event 'id' should not be null or negative");

        assertThat(invalidEventIds).allSatisfy(throwForInvalidId);
    }

    @Test
    public void shouldNotEnableOverwriteEventIdForNewInstance() {
      final var newEvent = EventBuilder.builder().build().newEvent();
      newEvent.setId(1L);
      assertThatThrownBy(() -> newEvent.setId(2L))
              .isInstanceOf(IllegalArgumentException.class)
              .hasMessage("Event 'id' has already been set");
    }

  @Test
  public void shouldNotEnableOverwriteEventIdForDeserializedInstance() {
    final var newEvent = EventBuilder.builder().build().deserialize();
    assertThatThrownBy(() -> newEvent.setId(1L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Event 'id' has already been set");
  }
}
