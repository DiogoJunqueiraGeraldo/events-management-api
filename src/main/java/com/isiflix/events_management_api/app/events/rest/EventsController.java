package com.isiflix.events_management_api.app.events.rest;

import com.isiflix.events_management_api.app.events.*;
import com.isiflix.events_management_api.app.events.dtos.CreateEventDTO;
import com.isiflix.events_management_api.app.shared.PaginationResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestController
@RequestMapping("/events")
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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventResponse createNewEvent(@Valid @RequestBody CreateEventRequest createEventRequest) {
        final var createEventDTO = CreateEventDTO.of(createEventRequest);
        final var createdEvent = createEventUseCase.createNewEvent(createEventDTO);
        return EventResponse.of(createdEvent);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public PaginationResponse<EventResponse> findAllEvents(
            @Valid
            @Positive(message = "Query Param 'page' must be positive")
            @RequestParam(required = false, defaultValue = "1")
            int page,

            @Valid
            @Positive(message = "Query Param 'size' must be positive")
            @RequestParam(required = false, defaultValue = "50")
            int size
    ) {
        final var items = listEventsUseCase.list(page, size)
                .stream()
                .map(EventResponse::of)
                .toList();

        return PaginationResponse.from(items, page, items.size());
    }

    @GetMapping("/{prettyName}")
    public ResponseEntity<EventResponse> findEvent(@PathVariable String prettyName) throws NoResourceFoundException {
        final var event = findEventUseCase.find(prettyName);
        return event
                .map(EventResponse::of)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new NoResourceFoundException(HttpMethod.GET, "/events/".concat(prettyName)));
    }
}
