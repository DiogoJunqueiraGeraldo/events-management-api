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
