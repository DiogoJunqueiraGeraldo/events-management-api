package com.isiflix.events_management_api.app.events.controllers;

import com.isiflix.events_management_api.app.events.dtos.CreateEventDTO;
import com.isiflix.events_management_api.app.events.dtos.EventDTO;
import com.isiflix.events_management_api.app.events.use_cases.CreateEventUseCase;
import com.isiflix.events_management_api.app.events.use_cases.FindEventUseCase;
import com.isiflix.events_management_api.app.events.use_cases.ListEventsUseCase;
import com.isiflix.events_management_api.app.shared.dtos.PaginationResultDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestController
public class EventsController {
    private final CreateEventUseCase createEventUseCase;
    private final ListEventsUseCase listEventsUseCase;
    private final FindEventUseCase findEventUseCase;

    @Autowired
    public EventsController(CreateEventUseCase createEventUseCase,
                            ListEventsUseCase listEventsUseCase,
                            FindEventUseCase findEventUseCase) {
        this.createEventUseCase = createEventUseCase;
        this.listEventsUseCase = listEventsUseCase;
        this.findEventUseCase = findEventUseCase;
    }

    @PostMapping("/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventDTO createNewEvent(@Valid @RequestBody CreateEventRequest createEventRequest) {
        final var createEventDTO = CreateEventDTO.of(createEventRequest);
        return createEventUseCase.createNewEvent(createEventDTO);
    }

    @GetMapping("/events")
    @ResponseStatus(HttpStatus.OK)
    public PaginationResultDTO<EventDTO> findAllEvents(
            @Valid
            @Positive(message = "Query Param 'page' must be positive")
            @RequestParam(required = false, defaultValue = "1")
            int page,

            @Valid
            @Positive(message = "Query Param 'size' must be positive")
            @RequestParam(required = false, defaultValue = "50")
            int size
    ) {
        final var items = listEventsUseCase.list(page, size);
        return PaginationResultDTO.from(items, page, items.size());
    }

    @GetMapping("/events/{prettyName}")
    public ResponseEntity<EventDTO> findEvent(@PathVariable String prettyName) throws NoResourceFoundException {
        final var event = findEventUseCase.find(prettyName);
        return event.map(ResponseEntity::ok)
                .orElseThrow(() -> new NoResourceFoundException(HttpMethod.GET, "/events/".concat(prettyName)));
    }
}
