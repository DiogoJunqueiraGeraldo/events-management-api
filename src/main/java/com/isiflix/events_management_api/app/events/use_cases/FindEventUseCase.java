package com.isiflix.events_management_api.app.events.use_cases;

import com.isiflix.events_management_api.app.events.dtos.EventDTO;
import com.isiflix.events_management_api.domain.events.EventRepository;
import com.isiflix.events_management_api.domain.events.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FindEventUseCase {
    private final EventService eventService;

    @Autowired
    public FindEventUseCase(EventRepository eventRepository) {
        this.eventService = new EventService(eventRepository);
    }

    public Optional<EventDTO> find(String prettyName) {
        return eventService.findByPrettyName(prettyName);
    }
}
