package com.isiflix.events_management_api.domain.events;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class EventService {
    private final EventRepository eventRepository;

    @Autowired
    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public void save(Event event) {
        Objects.requireNonNull(event, "Cannot save 'event' that is null");
        Long id = this.eventRepository.save(event);
        event.setId(id);
    }
}
