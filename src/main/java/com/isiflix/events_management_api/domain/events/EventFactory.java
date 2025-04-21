package com.isiflix.events_management_api.domain.events;

import com.isiflix.events_management_api.app.events.dtos.CreateEventDTO;
import com.isiflix.events_management_api.app.events.dtos.EventDTO;
import com.isiflix.events_management_api.domain.events.vos.EventPeriod;
import com.isiflix.events_management_api.domain.events.vos.EventPrettyName;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class EventFactory {

    public static Event create(CreateEventDTO createEventDTO) {
        return new Event(
                createEventDTO.name(),
                createEventDTO.location(),
                createEventDTO.price(),
                new EventPeriod(
                        createEventDTO.startDateTime(),
                        createEventDTO.endDateTime()
                )
        );
    }


    /**
     * Reconstitutes an {@link Event} entity using raw domain values from storage.
     * <p>
     * This factory method rebuilds an event directly from its stored raw values,
     * avoiding any dependency on external DTOs. It ensures that the reconstructed event
     * complies with all domain invariants.
     *
     * @param id            the event's unique identifier.
     * @param name          the event name.
     * @param prettyName    the human-friendly event name.
     * @param location      the event location.
     * @param price         the event price.
     * @param startDateTime the event start date and time.
     * @param endDateTime   the event end date and time.
     * @return an {@link Event} instance reconstructed from the provided raw values.
     * @throws IllegalArgumentException if any provided value violates domain rules.
     */
    public static Event fromRaw(Long id,
                                String name,
                                String prettyName,
                                String location,
                                BigDecimal price,
                                LocalDateTime startDateTime,
                                LocalDateTime endDateTime) {
        return new Event(
                id,
                name,
                EventPrettyName.of(prettyName),
                location,
                price,
                new EventPeriod(startDateTime, endDateTime)
        );
    }

    public static Event of(EventDTO eventDTO) {
        return fromRaw(
                eventDTO.id(),
                eventDTO.name(),
                eventDTO.prettyName(),
                eventDTO.location(),
                eventDTO.price(),
                eventDTO.startDateTime(),
                eventDTO.endDateTime()
        );
    }
}
