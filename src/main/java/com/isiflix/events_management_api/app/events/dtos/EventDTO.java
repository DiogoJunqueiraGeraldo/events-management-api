package com.isiflix.events_management_api.app.events.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EventDTO(
        Long id,
        String name,
        String prettyName,
        String location,
        BigDecimal price,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime
) { }
