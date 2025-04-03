package com.isiflix.events_management_api.app.events.use_cases;

import com.isiflix.events_management_api.app.events.dtos.CreateEventDTO;
import com.isiflix.events_management_api.app.events.dtos.EventDTO;
import com.isiflix.events_management_api.domain.events.EventFactory;
import com.isiflix.events_management_api.domain.events.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreateEventUseCase {
    private final EventService eventService;

    @Autowired
    public CreateEventUseCase(EventService eventService) {
        this.eventService = eventService;
    }

    public EventDTO createNewEvent(CreateEventDTO createNewEvent) {
        final var event = EventFactory.create(createNewEvent);
        eventService.save(event);
        return event.toDTO();
    }
}
