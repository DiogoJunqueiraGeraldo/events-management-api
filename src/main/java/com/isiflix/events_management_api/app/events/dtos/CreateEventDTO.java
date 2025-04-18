package com.isiflix.events_management_api.app.events.dtos;

import com.isiflix.events_management_api.app.events.rest.requests.CreateEventRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateEventDTO(
        String name,
        String location,
        BigDecimal price,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime
) {
    public static CreateEventDTO of(CreateEventRequest req) {
        return new CreateEventDTO(
                req.name(),
                req.location(),
                req.price(),
                req.startDate().atTime(req.startTime()),
                req.endDate().atTime(req.endTime())
        );
    }
}
