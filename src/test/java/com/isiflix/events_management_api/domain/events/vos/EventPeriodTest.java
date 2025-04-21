package com.isiflix.events_management_api.domain.events.vos;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

public class EventPeriodTest {
    public static final long VALID_EVENT_DURATION_IN_MINUTES = Math.max(EventPeriod.MIN_DURATION_IN_MINUTES, EventPeriod.MAX_DURATION_IN_MINUTES / 2);

    @Test
    public void shouldConstructValidEventPeriod() {
        final var startDatetime = LocalDateTime.now();
        final var endDatetime = LocalDateTime.now().plusMinutes(VALID_EVENT_DURATION_IN_MINUTES);
        final var eventPeriod = new EventPeriod(startDatetime, endDatetime);

        assertThat(eventPeriod.getDuration()).isCloseTo(Duration.ofMinutes(VALID_EVENT_DURATION_IN_MINUTES), Duration.ofMillis(1));
        assertThat(eventPeriod.startDateTime()).isEqualTo(startDatetime);
        assertThat(eventPeriod.endDateTime()).isEqualTo(endDatetime);
    }

    @Test
    public void shouldRequireEventPeriod() {
        assertThatThrownBy(() -> new EventPeriod(null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Event should have a start and an end");
    }

    @Test
    public void shouldRequireEventPeriodStartDateTime() {
        assertThatThrownBy(() -> new EventPeriod(LocalDateTime.now(), null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Event should have a start and an end");
    }
    @Test
    public void shouldRequireEventPeriodEndDateTime() {
        assertThatThrownBy(() -> new EventPeriod(null, LocalDateTime.now()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Event should have a start and an end");
    }

    @Test
    public void shouldValidateIfEventStartDateTimeIsBeforeThanEndDateTime() {
        final var endDatetime = LocalDateTime.now();
        final var startDatetime = endDatetime.plusHours(1);

        assertThatThrownBy(() -> new EventPeriod(startDatetime, endDatetime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Event cannot start after it ends");
    }

    @Test
    public void shouldValidateEventMinPeriod() {
        final var startDatetime = LocalDateTime.now();
        final var endDatetime = startDatetime.plusSeconds((EventPeriod.MIN_DURATION_IN_MINUTES * 60) - 1);

        assertThatThrownBy(() -> new EventPeriod(startDatetime, endDatetime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Event duration must be at least "
                        + EventPeriod.MIN_DURATION_IN_MINUTES + " minutes.");
    }

    @Test
    public void shouldValidateEventMaxPeriod() {
        final var startDatetime = LocalDateTime.now();
        final var endDatetime = startDatetime.plusMinutes(EventPeriod.MAX_DURATION_IN_MINUTES + 1);

        assertThatThrownBy(() -> new EventPeriod(startDatetime, endDatetime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Event duration must be no more than "
                        + EventPeriod.MAX_DURATION_IN_MINUTES + " minutes ("
                        + Duration.ofMinutes(EventPeriod.MAX_DURATION_IN_MINUTES).toDays() + " days).");
    }
}
