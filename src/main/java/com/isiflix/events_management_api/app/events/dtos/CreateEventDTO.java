package com.isiflix.events_management_api.app.events.dtos;

import com.isiflix.events_management_api.app.events.controllers.CreateEventRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record CreateEventDTO(
        String name,
        String location,
        BigDecimal price,
        LocalDate startDate,
        LocalDate endDate,
        LocalTime startTime,
        LocalTime endTime
) {
    public static CreateEventDTO of(CreateEventRequest req) {
        return new CreateEventDTO(
                req.name(),
                req.location(),
                req.price(),
                req.startDate(),
                req.endDate(),
                req.startTime(),
                req.endTime()
        );
    }
}
