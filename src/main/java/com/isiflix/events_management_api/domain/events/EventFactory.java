package com.isiflix.events_management_api.domain.events;

import com.isiflix.events_management_api.app.events.dtos.CreateEventDTO;
import com.isiflix.events_management_api.app.events.dtos.EventDTO;
import com.isiflix.events_management_api.domain.events.vos.EventPeriodVO;
import com.isiflix.events_management_api.domain.events.vos.PrettyNameVO;

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
     * Reconstitutes an {@link Event} entity from an existing {@link EventDTO}.
     * <p>
     * This method reconstructs an event using data typically retrieved from the persistence layer.
     *
     * @param eventDTO the DTO representing an existing event; must not be {@code null}.
     * @return an instantiated {@link Event} entity corresponding to the given DTO.
     * @throws IllegalArgumentException if the DTO data violates any domain invariants.
     */
    public static Event from(EventDTO eventDTO) {
        return new Event(
                eventDTO.id(),
                eventDTO.name(),
                PrettyNameVO.of(eventDTO.name()),
                eventDTO.location(),
                eventDTO.price(),
                new EventPeriodVO(
                        eventDTO.startDate(),
                        eventDTO.startTime(),
                        eventDTO.endDate(),
                        eventDTO.endTime()
                )
        );
    }
}
