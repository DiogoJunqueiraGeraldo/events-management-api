package com.isiflix.events_management_api.infra.database.memory.models;

import com.isiflix.events_management_api.app.events.dtos.EventDTO;
import com.isiflix.events_management_api.domain.events.Event;
import com.isiflix.events_management_api.domain.events.EventFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record EventRecord(
        Long id,
        String name,
        String prettyName,
        String location,
        BigDecimal price,
        LocalDate startDate,
        LocalDate endDate,
        LocalTime startTime,
        LocalTime endTime
) {
    public static EventRecord of(Long id, EventDTO eventDTO) {
        return new EventRecord(
                id,
                eventDTO.name(),
                eventDTO.prettyName(),
                eventDTO.location(),
                eventDTO.price(),
                eventDTO.startDate(),
                eventDTO.endDate(),
                eventDTO.startTime(),
                eventDTO.endTime()
        );
    }

    public Event toEntity() {
        return EventFactory.fromRaw(
                this.id,
                this.name,
                this.prettyName,
                this.location,
                this.price,
                this.startDate,
                this.endDate,
                this.startTime,
                this.endTime
        );
    }
}
