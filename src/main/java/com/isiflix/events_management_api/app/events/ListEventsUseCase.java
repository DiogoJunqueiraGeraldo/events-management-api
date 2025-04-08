package com.isiflix.events_management_api.app.events;

import com.isiflix.events_management_api.app.events.dtos.EventDTO;
import com.isiflix.events_management_api.domain.events.Event;
import com.isiflix.events_management_api.domain.events.EventRepository;
import com.isiflix.events_management_api.domain.events.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListEventsUseCase {
    private final EventService eventService;

    @Autowired
    public ListEventsUseCase(EventRepository eventRepository) {
        this.eventService = new EventService(eventRepository);
    }

    public List<EventDTO> list(int page, int size) {
        return this.eventService.list(page, size)
                .stream()
                .map(Event::toDTO)
                .toList();
    }
}
