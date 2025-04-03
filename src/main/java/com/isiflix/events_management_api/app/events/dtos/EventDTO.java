package com.isiflix.events_management_api.app.events.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record EventDTO(
        Long id,
        String name,
        String prettyName,
        String location,
        BigDecimal price,
        LocalDate startDate,
        LocalDate endDate,
        LocalTime startTime,
        LocalTime endTime
) { }
