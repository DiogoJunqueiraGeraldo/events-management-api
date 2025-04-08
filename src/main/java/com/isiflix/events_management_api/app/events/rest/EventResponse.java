package com.isiflix.events_management_api.app.events.rest;

import com.isiflix.events_management_api.app.events.dtos.EventDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record EventResponse(
        Long id,
        String name,
        String prettyName,
        String location,
        BigDecimal price,
        LocalDate startDate,
        LocalTime startTime,
        LocalDate endDate,
        LocalTime endTime
) {
    public static EventResponse of(EventDTO dto) {
        return new EventResponse(
                dto.id(),
                dto.name(),
                dto.prettyName(),
                dto.location(),
                dto.price(),
                dto.startDateTime().toLocalDate(),
                dto.startDateTime().toLocalTime(),
                dto.endDateTime().toLocalDate(),
                dto.endDateTime().toLocalTime()
        );
    }
}