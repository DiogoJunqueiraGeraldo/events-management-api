package com.isiflix.events_management_api.app.events.rest.requests;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record CreateEventRequest(
        @NotBlank(message = "The 'name' field is required and cannot be empty or blank.")
        String name,

        @NotBlank(message = "The 'location' field is required and cannot be empty or blank.")
        String location,

        @NotNull(message = "The 'price' of the event should not be null")
        @PositiveOrZero(message = "The 'price' field should not be negative.")
        BigDecimal price,

        @NotNull(message = "The 'startDate' of the event should not be null")
        @FutureOrPresent(message = "The 'startDate' of the event should be in the future or present")
        LocalDate startDate,

        @NotNull(message = "The 'endDate' of the event should not be null")
        @FutureOrPresent(message = "The 'endDate' of the event should be in the future or present")
        LocalDate endDate,

        @NotNull(message = "The 'startTime' of the event should not be null")
        @JsonFormat(pattern = "HH:mm:ss", shape = JsonFormat.Shape.STRING)
        LocalTime startTime,

        @NotNull(message = "The 'endTime' of the event should not be null")
        @JsonFormat(pattern = "HH:mm:ss", shape = JsonFormat.Shape.STRING)
        LocalTime endTime
) { }
