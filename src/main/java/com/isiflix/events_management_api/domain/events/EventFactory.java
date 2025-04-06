package com.isiflix.events_management_api.domain.events;

import com.isiflix.events_management_api.app.events.dtos.CreateEventDTO;
import com.isiflix.events_management_api.domain.events.vos.EventPeriodVO;
import com.isiflix.events_management_api.domain.events.vos.PrettyNameVO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public class EventFactory {
    /**
     * Creates a new {@link Event} entity based on the provided {@link CreateEventDTO}.
     * <p>
     *
     * @param createEventDTO the DTO containing details required to create a new event; must not be {@code null}.
     * @return a new {@link Event} entity.
     * @throws IllegalArgumentException if the provided DTO contains invalid or inconsistent data.
     */
    public static Event create(CreateEventDTO createEventDTO) {
        return new Event(
                createEventDTO.name(),
                createEventDTO.location(),
                createEventDTO.price(),
                new EventPeriodVO(
                        createEventDTO.startDate(),
                        createEventDTO.startTime(),
                        createEventDTO.endDate(),
                        createEventDTO.endTime()
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
     * @param id         the event's unique identifier.
     * @param name       the event name.
     * @param prettyName the human-friendly event name.
     * @param location   the event location.
     * @param price      the event price.
     * @param startDate  the event start date.
     * @param endDate    the event end date.
     * @param startTime  the event start time.
     * @param endTime    the event end time.
     * @return an {@link Event} instance reconstructed from the provided raw values.
     * @throws IllegalArgumentException if any provided value violates domain rules.
     */
    public static Event fromRaw(Long id,
                                String name,
                                String prettyName,
                                String location,
                                BigDecimal price,
                                LocalDate startDate,
                                LocalDate endDate,
                                LocalTime startTime,
                                LocalTime endTime) {
        return new Event(
                id,
                name,
                PrettyNameVO.of(prettyName),
                location,
                price,
                new EventPeriodVO(startDate, startTime, endDate, endTime)
        );
    }
}
