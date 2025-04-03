package com.isiflix.events_management_api.app.events.controllers;

import com.isiflix.events_management_api.app.events.dtos.CreateEventDTO;
import com.isiflix.events_management_api.app.events.dtos.EventDTO;
import com.isiflix.events_management_api.app.events.use_cases.CreateEventUseCase;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.util.Objects;

@RestController
public class EventsController {
    private final CreateEventUseCase createEventUseCase;

    @Autowired
    public EventsController(CreateEventUseCase createEventUseCase) {
        this.createEventUseCase = createEventUseCase;
    }

    @PostMapping("/events")
    public ResponseEntity<EventDTO> createNewEvent(@Valid @RequestBody CreateEventRequest request) {
        final var dto = CreateEventDTO.of(request);
        final var event = createEventUseCase.createNewEvent(dto);
        return ResponseEntity.status(201).body(event);
    }
}
