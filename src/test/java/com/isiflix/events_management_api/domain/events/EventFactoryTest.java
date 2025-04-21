package com.isiflix.events_management_api.domain.events;

import com.isiflix.events_management_api.app.events.dtos.CreateEventDTO;
import com.isiflix.events_management_api.app.events.dtos.EventDTO;
import com.isiflix.events_management_api.domain.events.vos.EventPeriodTest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class EventFactoryTest {
    @Test
    public void shouldCreateNewEvent() {
        final var referenceDateTime = LocalDateTime.now();
        final var dto = new CreateEventDTO(
                "Foo Bar",
                "online",
                BigDecimal.ZERO,
                referenceDateTime,
                referenceDateTime.plusMinutes(EventPeriodTest.VALID_EVENT_DURATION_IN_MINUTES)
        );

        final var event = EventFactory.create(dto);
        assertThat(event).isNotNull();
        assertThat(event.getId()).isNull();
        assertThat(event.getPrettyName()).isEqualTo("foo-bar");
    }

    @Test
    public void shouldDeserializeEvent() {
        final var referenceDateTime = LocalDateTime.now();
        final var event = EventFactory.fromRaw(
                1L,
                "Foo Bar",
                "dont-recalculate",
                "online",
                BigDecimal.TEN,
                referenceDateTime,
                referenceDateTime.plusMinutes(EventPeriodTest.VALID_EVENT_DURATION_IN_MINUTES)
        );

        assertThat(event).isNotNull();
        assertThat(event.getId()).isEqualTo(1L);
        assertThat(event.getPrettyName()).isEqualTo("dont-recalculate");
    }

    @Test
    public void shouldReconstructEvent() {
        final var referenceDateTime = LocalDateTime.now();
        final var dto = new EventDTO(
                2L,
                "Foo Bar",
                "also-dont-recalculate",
                "online",
                BigDecimal.ZERO,
                referenceDateTime,
                referenceDateTime.plusMinutes(EventPeriodTest.VALID_EVENT_DURATION_IN_MINUTES)
        );

        final var event = EventFactory.of(dto);
        assertThat(event).isNotNull();
        assertThat(event.getId()).isEqualTo(2L);
        assertThat(event.getPrettyName()).isEqualTo("also-dont-recalculate");
    }
}
