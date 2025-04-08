package com.isiflix.events_management_api.domain.events.vos;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * It represents the event duration
 * <p/>
 * This value object ensures that an event has a valid start and end time,
 * with constraints on minimum and maximum duration.
 */
public record EventPeriodVO(LocalDateTime startDateTime, LocalDateTime endDateTime) {
    public static final long MIN_DURATION_IN_MINUTES = 1;
    public static final long MAX_DURATION_IN_MINUTES = Duration.ofDays(365).toMinutes();

    public EventPeriodVO(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        validateRequirements();
        validateCoherence();
        validateConstraints();
    }

    public Duration getDuration() {
        return Duration.between(startDateTime, endDateTime);
    }

    private void validateRequirements() {
        if (this.startDateTime == null || this.endDateTime == null) {
            throw new IllegalArgumentException("Event should have a start and an end");
        }
    }

    private void validateCoherence() {
        if (startDateTime.isAfter(endDateTime)) {
            throw new IllegalArgumentException("Event cannot start after it ends");
        }
    }

    private void validateConstraints() {
        final var durationInMinutes = getDuration().toMinutes();

        if (durationInMinutes < MIN_DURATION_IN_MINUTES) {
            throw new IllegalArgumentException("Event duration must be at least "
                    + MIN_DURATION_IN_MINUTES + " minutes.");
        }

        if (durationInMinutes > MAX_DURATION_IN_MINUTES) {
            throw new IllegalArgumentException("Event duration must be no more than "
                    + MAX_DURATION_IN_MINUTES + " minutes ("
                    + Duration.ofMinutes(MAX_DURATION_IN_MINUTES).toDays() + " days).");
        }
    }
}
